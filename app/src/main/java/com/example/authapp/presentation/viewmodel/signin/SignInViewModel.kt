package com.example.authapp.presentation.viewmodel.signin

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.core.utils.Resource
import com.example.authapp.data.local.UserPreferencesDataStore
import com.example.authapp.data.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class SignInViewModel(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _toastMessage = MutableStateFlow("")
    val toastMessage: StateFlow<String> = _toastMessage.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError: StateFlow<Boolean> = _passwordError.asStateFlow()

    private val _emailTouched = MutableStateFlow(false)
    private val _passwordTouched = MutableStateFlow(false)

    private val _signInSuccess = MutableSharedFlow<Unit>()
    val signInSuccess: SharedFlow<Unit> = _signInSuccess.asSharedFlow()

    private val _startGoogleSignIn = MutableSharedFlow<Unit>()
    val startGoogleSignIn: SharedFlow<Unit> = _startGoogleSignIn.asSharedFlow()

    private val _navigateToForgotPassword = MutableSharedFlow<Unit>()
    val navigateToForgotPassword: SharedFlow<Unit> = _navigateToForgotPassword.asSharedFlow()

    init {
        loadUserPreferences()
    }

    private fun loadUserPreferences() {
        viewModelScope.launch {
            userPreferencesDataStore.userPreferences.collect { prefs ->
                if (prefs.rememberMe) {
                    _email.value = prefs.lastEmail
                    _rememberMe.value = true
                }
            }
        }
    }

    private val areFieldsValid = combine(
        _email,
        _password,
        _emailError,
        _passwordError
    ) { email, password, emailError, passwordError ->
        email.trim().isNotBlank() &&
                password.trim().isNotBlank() &&
                !emailError &&
                !passwordError &&
                isValidEmail(email.trim()) &&
                isValidPassword(password.trim())
    }

    val isSignInEnabled = areFieldsValid
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _emailTouched.value = true
        if (_emailTouched.value) {
            validateEmailRealTime(newEmail)
        }
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _passwordTouched.value = true
        if (_passwordTouched.value) {
            validatePasswordRealTime(newPassword)
        }
    }

    fun onToggleRememberMe() {
        _rememberMe.value = !_rememberMe.value
    }

    fun onTogglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun onSignIn() {
        if (!isSignInEnabled.value) return

        viewModelScope.launch {
            supervisorScope {
                _isLoading.value = true
                _errorMessage.value = ""

                try {
                    if (_rememberMe.value) {
                        userPreferencesDataStore.setRememberMe(true, _email.value.trim())
                    } else {
                        userPreferencesDataStore.setRememberMe(false)
                    }

                    when (val result =
                        authRepository.signIn(_email.value.trim(), _password.value.trim())) {
                        is Resource.Success -> {
                            val user = result.data.user
                            if (user != null) {
                                userPreferencesDataStore.saveUserData(
                                    userId = user.uid,
                                    userName = user.displayName ?: "",
                                    userEmail = user.email ?: "",
                                    userPhotoUrl = user.photoUrl?.toString()
                                )
                            }
                            _signInSuccess.emit(Unit)
                        }

                        is Resource.Failure -> {
                            _errorMessage.value = result.throwable.message ?: "Sign in failed"
                        }

                        is Resource.Loading -> { /* handled by isLoading */ }
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Sign in failed: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun onGoogleSignIn() {
        viewModelScope.launch {
            _startGoogleSignIn.emit(Unit)
        }
    }

    // Novo método para lidar com o ID Token do Google
    fun handleGoogleSignInResult(idToken: String?) {
        viewModelScope.launch {
            supervisorScope {
                _isLoading.value = true
                _errorMessage.value = ""

                try {
                    if (idToken == null) {
                        _errorMessage.value = "Google sign in was cancelled"
                        return@supervisorScope
                    }

                    when (val result = authRepository.signInWithGoogleIdToken(idToken)) {
                        is Resource.Success -> {
                            val user = result.data
                            userPreferencesDataStore.saveUserData(
                                userId = user.id,
                                userName = user.name ?: "",
                                userEmail = user.email ?: "",
                                userPhotoUrl = user.photoUrl
                            )
                            _signInSuccess.emit(Unit)
                        }

                        is Resource.Failure -> {
                            _errorMessage.value =
                                result.throwable.message ?: "Google sign in failed"
                        }

                        is Resource.Loading -> { /* handled by isLoading */ }
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Google sign in failed: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun onEmailFocusLost() {
        _emailTouched.value = true
        validateEmail()
    }

    fun onPasswordFocusLost() {
        _passwordTouched.value = true
        validatePassword()
    }

    private fun validateEmailRealTime(email: String) {
        val trimmedEmail = email.trim()
        _emailError.value = if (trimmedEmail.isEmpty()) {
            false
        } else {
            !isValidEmail(trimmedEmail)
        }
    }

    private fun validatePasswordRealTime(password: String) {
        val trimmedPassword = password.trim()
        _passwordError.value = if (trimmedPassword.isEmpty()) {
            false
        } else {
            !isValidPassword(trimmedPassword)
        }
    }

    private fun validateEmail() {
        val trimmedEmail = _email.value.trim()
        _emailError.value = trimmedEmail.isEmpty() || !isValidEmail(trimmedEmail)
    }

    private fun validatePassword() {
        val trimmedPassword = _password.value.trim()
        _passwordError.value = trimmedPassword.isEmpty() || !isValidPassword(trimmedPassword)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}