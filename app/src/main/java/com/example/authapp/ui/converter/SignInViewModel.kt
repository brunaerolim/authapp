package com.example.authapp.ui.converter

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.AuthRepository
import com.example.authapp.utils.Resource
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _showLoading = MutableStateFlow(false)
    val showLoading: StateFlow<Boolean> = _showLoading.asStateFlow()

    private val _errorToastMessage = MutableStateFlow("")
    val errorToastMessage: StateFlow<String> = _errorToastMessage.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError: StateFlow<Boolean> = _passwordError.asStateFlow()

    private val _emailTouched = MutableStateFlow(false)
    private val _passwordTouched = MutableStateFlow(false)

    // Combined States
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

    // Events
    private val _signInSuccess = MutableSharedFlow<Unit>()
    val signInSuccess: SharedFlow<Unit> = _signInSuccess.asSharedFlow()

    private val _googleSignInRequest = MutableSharedFlow<Unit>()
    val googleSignInRequest: SharedFlow<Unit> = _googleSignInRequest.asSharedFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
        _emailTouched.value = true

        if (_emailTouched.value) {
            validateEmailRealTime(newEmail)
        }
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
        _passwordTouched.value = true

        if (_passwordTouched.value) {
            validatePasswordRealTime(newPassword)
        }
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

    fun validateEmail() {
        _emailTouched.value = true
        val trimmedEmail = _email.value.trim()
        _emailError.value = trimmedEmail.isEmpty() || !isValidEmail(trimmedEmail)
    }

    fun validatePassword() {
        _passwordTouched.value = true
        val trimmedPassword = _password.value.trim()
        _passwordError.value = trimmedPassword.isEmpty() || !isValidPassword(trimmedPassword)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun signIn() {
        if (!isSignInEnabled.value) return

        viewModelScope.launch {
            _showLoading.value = true

            when (val result = authRepository.signIn(_email.value.trim(), _password.value.trim())) {
                is Resource.Success -> {
                    _signInSuccess.emit(Unit)
                }

                is Resource.Failure -> {
                    _errorToastMessage.value = result.exception.message ?: "Sign in failed"
                }

                is Resource.Loading -> { /* handled by showLoading */
                }
            }

            _showLoading.value = false
        }
    }

    fun requestGoogleSignIn() {
        viewModelScope.launch {
            _googleSignInRequest.emit(Unit)
        }
    }

    fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            _showLoading.value = true

            when (val result = authRepository.signInWithGoogle(credential)) {
                is Resource.Success -> {
                    _signInSuccess.emit(Unit)
                }

                is Resource.Failure -> {
                    _errorToastMessage.value = result.exception.message ?: "Google sign in failed"
                }

                is Resource.Loading -> { /* handled by showLoading */
                }
            }

            _showLoading.value = false
        }
    }

    fun dismissErrorMessage() {
        _errorToastMessage.value = ""
    }
}