package com.example.authapp.presentation.viewmodel.signup

import android.util.Patterns
import androidx.credentials.GetCredentialResponse
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

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _nameError = MutableStateFlow(false)
    val nameError: StateFlow<Boolean> = _nameError.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError: StateFlow<Boolean> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow(false)
    val confirmPasswordError: StateFlow<Boolean> = _confirmPasswordError.asStateFlow()

    private val _showLoading = MutableStateFlow(false)
    val showLoading: StateFlow<Boolean> = _showLoading.asStateFlow()

    private val _errorToastMessage = MutableStateFlow("")
    val errorToastMessage: StateFlow<String> = _errorToastMessage.asStateFlow()

    private val _signUpSuccess = MutableSharedFlow<Unit>()
    val signUpSuccess: SharedFlow<Unit> = _signUpSuccess.asSharedFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _isConfirmPasswordVisible = MutableStateFlow(false)
    val isConfirmPasswordVisible: StateFlow<Boolean> = _isConfirmPasswordVisible.asStateFlow()

    private val _acceptTerms = MutableStateFlow(false)
    val acceptTerms: StateFlow<Boolean> = _acceptTerms.asStateFlow()

    private val _startGoogleSignUp = MutableSharedFlow<Unit>()
    val startGoogleSignUp: SharedFlow<Unit> = _startGoogleSignUp.asSharedFlow()

    private val _navigateToBack = MutableSharedFlow<Unit>()
    val navigateToBack: SharedFlow<Unit> = _navigateToBack.asSharedFlow()

    fun onGoogleSignUp() {
        viewModelScope.launch {
            _startGoogleSignUp.emit(Unit)
        }
    }

    val signUpEnabled: StateFlow<Boolean> = combine(
        _name,
        _email,
        _password,
        _confirmPassword,
        _showLoading
    ) { name, email, password, confirmPassword, loading ->
        name.trim().length >= 2 &&
                email.trim().isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
                password.trim().length >= 6 &&
                confirmPassword.trim() == password.trim() &&
                !loading
    }.combine(_acceptTerms) { basicValidation, terms ->
        basicValidation && terms
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun onNameChanged(newName: String) {
        _name.value = newName
        if (_nameError.value) {
            _nameError.value = newName.trim().length < 2
        }
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
        if (_emailError.value) {
            _emailError.value = !Patterns.EMAIL_ADDRESS.matcher(newEmail.trim()).matches()
        }
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        if (_passwordError.value) {
            _passwordError.value = newPassword.trim().length < 6
        }
        if (_confirmPassword.value.isNotEmpty() && _confirmPasswordError.value) {
            _confirmPasswordError.value = _confirmPassword.value.trim() != newPassword.trim()
        }
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
        if (_confirmPasswordError.value) {
            _confirmPasswordError.value = newConfirmPassword.trim() != _password.value.trim()
        }
    }

    fun onNameFocusLost(isFocused: Boolean) {
        _nameError.value = _name.value.trim().length < 2
    }

    fun onEmailFocusLost(isFocused: Boolean) {
        _emailError.value = !Patterns.EMAIL_ADDRESS.matcher(_email.value.trim()).matches()
    }

    fun onPasswordFocusLost(isFocused: Boolean) {
        _passwordError.value = _password.value.trim().length < 6
    }

    fun onConfirmPasswordFocusLost(isFocused: Boolean) {
        _confirmPasswordError.value = _confirmPassword.value.trim() != _password.value.trim()
    }

    fun dismissSnackbar() {
        _errorToastMessage.value = ""
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        _isConfirmPasswordVisible.value = !_isConfirmPasswordVisible.value
    }

    fun onAcceptTermsChanged(accept: Boolean) {
        _acceptTerms.value = accept
    }

    fun signUp() {
        if (!signUpEnabled.value) return

        viewModelScope.launch {
            _showLoading.value = true

            when (val result = authRepository.signUp(
                _email.value.trim(),
                _password.value.trim(),
                _name.value.trim()
            )) {
                is Resource.Success -> {
                    val user = result.data.user
                    if (user != null) {
                        userPreferencesDataStore.saveUserData(
                            userId = user.uid,
                            userName = user.displayName ?: _name.value.trim(),
                            userEmail = user.email ?: _email.value.trim(),
                            userPhotoUrl = user.photoUrl?.toString()
                        )
                    }
                    _signUpSuccess.emit(Unit)
                }

                is Resource.Failure -> {
                    _errorToastMessage.value = result.throwable.message ?: "Sign up failed"
                }

                is Resource.Loading -> { /* handled by showLoading */
                }
            }

            _showLoading.value = false
        }
    }

    fun handleGoogleSignUpResult(credentialResponse: GetCredentialResponse) {
        viewModelScope.launch {
            _showLoading.value = true
            _errorToastMessage.value = ""

            try {
                when (val result = authRepository.signInWithGoogle(credentialResponse)) {
                    is Resource.Success -> {
                        val user = result.data
                        userPreferencesDataStore.saveUserData(
                            userId = user.id,
                            userName = user.name ?: "",
                            userEmail = user.email ?: "",
                            userPhotoUrl = user.photoUrl
                        )
                        _signUpSuccess.emit(Unit)
                    }

                    is Resource.Failure -> {
                        _errorToastMessage.value =
                            result.throwable.message ?: "Google sign up failed"
                    }

                    is Resource.Loading -> { /* handled by showLoading */
                    }
                }
            } catch (e: Exception) {
                _errorToastMessage.value = "Google sign up failed: ${e.message}"
            } finally {
                _showLoading.value = false
            }
        }
    }

    fun handleGoogleSignUpError(errorMessage: String) {
        viewModelScope.launch {
            _errorToastMessage.value = errorMessage
            _showLoading.value = false
        }
    }
}