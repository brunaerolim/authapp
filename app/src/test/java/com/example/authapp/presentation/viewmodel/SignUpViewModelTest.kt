package com.example.authapp.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.authapp.data.repository.AuthRepository
import com.example.authapp.data.local.UserPreferences
import com.example.authapp.data.local.UserPreferencesDataStore
import com.example.authapp.presentation.viewmodel.signup.SignUpViewModel
import com.example.authapp.core.utils.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository = mockk<AuthRepository>()
    private val userPreferencesDataStore = mockk<UserPreferencesDataStore>()

    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { userPreferencesDataStore.userPreferences } returns flowOf(
            UserPreferences(
                userId = "",
                userName = "",
                userEmail = "",
                userPhotoUrl = null,
                isLoggedIn = false,
                rememberMe = false,
                lastEmail = ""
            )
        )

        viewModel = SignUpViewModel(authRepository, userPreferencesDataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when name is changed, state is updated correctly`() = runTest {
        val testName = "John Doe"

        viewModel.onNameChanged(testName)

        assertEquals(testName, viewModel.name.value)
        assertFalse(viewModel.nameError.value)
    }

    @Test
    fun `when invalid name is entered and focus lost, error is shown`() = runTest {
        val invalidName = "J"

        viewModel.onNameChanged(invalidName)
        viewModel.onNameFocusLost(false)

        assertEquals(invalidName, viewModel.name.value)
        assertTrue(viewModel.nameError.value)
    }

    @Test
    fun `when email is changed, state is updated correctly`() = runTest {
        val testEmail = "test@example.com"

        viewModel.onEmailChanged(testEmail)

        assertEquals(testEmail, viewModel.email.value)
        assertFalse(viewModel.emailError.value)
    }

    @Test
    fun `when invalid email is entered and focus lost, error is shown`() = runTest {
        val invalidEmail = "invalid-email"

        viewModel.onEmailChanged(invalidEmail)
        viewModel.onEmailFocusLost(false)

        assertEquals(invalidEmail, viewModel.email.value)
        assertTrue(viewModel.emailError.value)
    }

    @Test
    fun `when password is changed, state is updated correctly`() = runTest {
        val testPassword = "password123"

        viewModel.onPasswordChanged(testPassword)

        assertEquals(testPassword, viewModel.password.value)
        assertFalse(viewModel.passwordError.value)
    }

    @Test
    fun `when passwords do not match, confirm password error is shown`() = runTest {
        val password = "password123"
        val confirmPassword = "different123"

        viewModel.onPasswordChanged(password)
        viewModel.onConfirmPasswordChanged(confirmPassword)
        viewModel.onConfirmPasswordFocusLost(false)

        assertTrue(viewModel.confirmPasswordError.value)
    }

    @Test
    fun `when passwords match, confirm password error is cleared`() = runTest {
        val password = "password123"

        viewModel.onPasswordChanged(password)
        viewModel.onConfirmPasswordChanged(password)
        viewModel.onConfirmPasswordFocusLost(false)

        assertFalse(viewModel.confirmPasswordError.value)
    }

    @Test
    fun `when accept terms is toggled, state is updated`() = runTest {
        assertFalse(viewModel.acceptTerms.value)

        viewModel.onAcceptTermsChanged(true)

        assertTrue(viewModel.acceptTerms.value)
    }

    @Test
    fun `when password visibility is toggled, state is updated`() = runTest {
        assertFalse(viewModel.isPasswordVisible.value)

        viewModel.togglePasswordVisibility()

        assertTrue(viewModel.isPasswordVisible.value)

        viewModel.togglePasswordVisibility()

        assertFalse(viewModel.isPasswordVisible.value)
    }

    @Test
    fun `signUpEnabled is false when fields are invalid`() = runTest {
        // Initially should be false
        assertFalse(viewModel.signUpEnabled.value)

        // Set invalid data
        viewModel.onNameChanged("J") // Too short
        viewModel.onEmailChanged("invalid-email")
        viewModel.onPasswordChanged("123") // Too short
        viewModel.onConfirmPasswordChanged("456") // Doesn't match
        viewModel.onAcceptTermsChanged(false)

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.signUpEnabled.value)
    }

    @Test
    fun `signUpEnabled is true when all fields are valid`() = runTest {
        // Set valid data
        viewModel.onNameChanged("John Doe")
        viewModel.onEmailChanged("john@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.onConfirmPasswordChanged("password123")
        viewModel.onAcceptTermsChanged(true)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.signUpEnabled.value)
    }

    @Test
    fun `when signUp is successful, user data is saved to DataStore`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val name = "Test User"

        val mockFirebaseUser = mockk<FirebaseUser> {
            every { uid } returns "test-uid"
            every { displayName } returns name
            every { email } returns email
            every { photoUrl } returns null
        }

        val mockAuthResult = mockk<AuthResult> {
            every { user } returns mockFirebaseUser
        }

        coEvery {
            authRepository.signUp(email, password, name)
        } returns Resource.Success(mockAuthResult)

        coEvery {
            userPreferencesDataStore.saveUserData(any(), any(), any(), any())
        } just Runs

        // Set up valid form data
        viewModel.onNameChanged(name)
        viewModel.onEmailChanged(email)
        viewModel.onPasswordChanged(password)
        viewModel.onConfirmPasswordChanged(password)
        viewModel.onAcceptTermsChanged(true)

        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.signUp()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify {
            userPreferencesDataStore.saveUserData(
                userId = "test-uid",
                userName = name,
                userEmail = email,
                userPhotoUrl = null
            )
        }
        assertFalse(viewModel.showLoading.value)
    }

    @Test
    fun `when signUp fails, error message is shown`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val name = "Test User"
        val errorMessage = "Sign up failed"

        coEvery {
            authRepository.signUp(email, password, name)
        } returns Resource.Failure(Exception(errorMessage))

        // Set up valid form data
        viewModel.onNameChanged(name)
        viewModel.onEmailChanged(email)
        viewModel.onPasswordChanged(password)
        viewModel.onConfirmPasswordChanged(password)
        viewModel.onAcceptTermsChanged(true)

        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.signUp()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(errorMessage, viewModel.errorToastMessage.value)
        assertFalse(viewModel.showLoading.value)
    }

    @Test
    fun `when dismissSnackbar is called, error message is cleared`() = runTest {
        // Set error message
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.onNameChanged("Test User")
        viewModel.onConfirmPasswordChanged("password123")
        viewModel.onAcceptTermsChanged(true)

        coEvery {
            authRepository.signUp(any(), any(), any())
        } returns Resource.Failure(Exception("Error"))

        viewModel.signUp()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify error is set
        assertTrue(viewModel.errorToastMessage.value.isNotEmpty())

        // Act
        viewModel.dismissSnackbar()

        // Assert
        assertTrue(viewModel.errorToastMessage.value.isEmpty())
    }
}