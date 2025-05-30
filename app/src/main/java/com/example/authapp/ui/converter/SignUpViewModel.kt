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
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
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

    val signUpEnabled: StateFlow<Boolean> = combine(
        _name, _email, _password, _confirmPassword, _showLoading
    ) { name, email, password, confirmPassword, loading ->
        name.trim().length >= 2 &&
                email.trim()
                    .isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim())
            .matches() &&
                password.trim().length >= 6 &&
                confirmPassword.trim() == password.trim() &&
                !loading
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
            _emailError.value =
                !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail.trim()).matches()
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
                    _signUpSuccess.emit(Unit)
                }

                is Resource.Failure -> {
                    _errorToastMessage.value = result.exception.message ?: "Sign up failed"
                }

                is Resource.Loading -> { /* handled by showLoading */
                }
            }

            _showLoading.value = false
        }
    }

    fun signUpWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            _showLoading.value = true

            when (val result = authRepository.signInWithGoogle(credential)) {
                is Resource.Success -> {
                    _signUpSuccess.emit(Unit)
                }

                is Resource.Failure -> {
                    _errorToastMessage.value = result.exception.message ?: "Google sign up failed"
                }

                is Resource.Loading -> { /* handled by showLoading */
                }
            }

            _showLoading.value = false
        }
    }
}