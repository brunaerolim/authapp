package com.example.authapp.presentation.viewmodel.signin.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForgotPasswordViewModel @Inject constructor() : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError.asStateFlow()

    private val _emailTouched = MutableStateFlow(false)

    private val _navigateToResetPassword = MutableSharedFlow<String>()

    private val _navigateToBack = MutableSharedFlow<Unit>()
    val navigateToBack: SharedFlow<Unit> = _navigateToBack.asSharedFlow()


    val isSendEmailEnabled = combine(
        _email,
        _emailError
    ) { email, emailError ->
        email.trim().isNotBlank() && !emailError && isValidEmail(email.trim())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun sendPasswordResetEmail() {
        viewModelScope.launch {
            _isLoading.update { true }
            clearMessages()

            FirebaseAuth.getInstance().sendPasswordResetEmail(_email.value.trim())
                .addOnCompleteListener { task ->
                    _isLoading.update { false }
                    if (task.isSuccessful) {
                        _successMessage.update { "Password reset email sent successfully!" }
                        viewModelScope.launch {
                            delay(1500) // Pequeno delay para mostrar a mensagem
                            _navigateToResetPassword.emit(_email.value.trim())
                        }
                    } else {
                        val errorMessage = when (task.exception?.message) {
                            "There is no user record corresponding to this identifier. The user may have been deleted." ->
                                "No account found with this email address"

                            "The email address is badly formatted." ->
                                "Invalid email address format"

                            else -> task.exception?.localizedMessage
                                ?: "Failed to send password reset email"
                        }
                        _errorMessage.update { errorMessage }
                    }
                }
        }
    }

    fun onNavigateBack() {
        viewModelScope.launch {
            _navigateToBack.emit(Unit)
        }
    }

    fun clearMessages() {
        _errorMessage.update { null }
        _successMessage.update { null }
    }

    private fun validateEmailRealTime(email: String) {
        val trimmedEmail = email.trim()
        _emailError.value = if (trimmedEmail.isEmpty()) {
            false
        } else {
            !isValidEmail(trimmedEmail)
        }
    }

    private fun validateEmail() {
        val trimmedEmail = _email.value.trim()
        _emailError.value = trimmedEmail.isEmpty() || !isValidEmail(trimmedEmail)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Event handlers

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _emailTouched.value = true
        if (_emailTouched.value) {
            validateEmailRealTime(newEmail)
        }
    }

    fun onEmailFocusLost() {
        _emailTouched.value = true
        validateEmail()
    }
}