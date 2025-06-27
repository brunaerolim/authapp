package com.example.authapp.presentation.viewmodel.signin.resetpassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError.asStateFlow()

    private val _emailTouched = MutableStateFlow(false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorToastMessage = MutableStateFlow("")
    val errorToastMessage: StateFlow<String> = _errorToastMessage.asStateFlow()

    private val _successToastMessage = MutableStateFlow("")
    val successToastMessage: StateFlow<String> = _successToastMessage.asStateFlow()

    val sendEnabled: StateFlow<Boolean> = combine(
        _email,
        _emailError,
        _isLoading
    ) { email, emailError, loading ->
        email.isNotBlank() && !emailError && !loading
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        if (_emailTouched.value) {
            _emailError.value = !Patterns.EMAIL_ADDRESS.matcher(newEmail.trim()).matches()
        }
    }

    fun onEmailFocusLost() {
        _emailTouched.value = true
        _emailError.value = !Patterns.EMAIL_ADDRESS.matcher(_email.value.trim()).matches()
    }

    fun sendPasswordResetEmail() {
        if (!sendEnabled.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorToastMessage.value = ""
            _successToastMessage.value = ""

            val result = authRepository.sendPasswordResetEmail(_email.value.trim())
            if (result.isSuccess) {
                _successToastMessage.value = "Reset email sent successfully"
            } else {
                _errorToastMessage.value =
                    result.exceptionOrNull()?.message ?: "Failed to send reset email"
            }

            _isLoading.value = false
        }
    }

    fun clearMessages() {
        _errorToastMessage.value = ""
        _successToastMessage.value = ""
    }
}