package com.example.authapp.ui.converter


import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.AuthRepository
import com.example.authapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI State Fields
    val showLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errorToastMessage: MutableStateFlow<String> = MutableStateFlow("")

    val name: MutableStateFlow<String> = MutableStateFlow("")
    val nameError: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val email: MutableStateFlow<String> = MutableStateFlow("")
    val emailError: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val password: MutableStateFlow<String> = MutableStateFlow("")
    val passwordError: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val confirmPassword: MutableStateFlow<String> = MutableStateFlow("")
    val confirmPasswordError: MutableStateFlow<Boolean> = MutableStateFlow(false)

    // Combined States
    private val signUpFieldsNotEmpty = combine(name, email, password, confirmPassword) { fields ->
        fields.all { it.isNotEmpty() }
    }

    private val signUpNoErrors =
        combine(nameError, emailError, passwordError, confirmPasswordError) { errors ->
            errors.all { !it }
        }

    val signUpEnabled = combine(signUpFieldsNotEmpty, signUpNoErrors) { fieldsNotEmpty, noErrors ->
        fieldsNotEmpty && noErrors
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    // Events
    private val _signUpSuccess = MutableSharedFlow<Unit>()
    val signUpSuccess: SharedFlow<Unit> = _signUpSuccess

    // Actions
    fun onSignUp() {
        validateAll()
        viewModelScope.launch {
            if (signUpEnabled.value) {
                showLoading.value = true
                when (val result = authRepository.signUpWithEmailAndPassword(
                    name.value.trim(),
                    email.value.trim(),
                    password.value.trim()
                )) {
                    is Resource.Success -> {
                        _signUpSuccess.emit(Unit)
                    }

                    is Resource.Failure -> {
                        errorToastMessage.value = result.exception.message ?: "Sign up failed"
                    }

                    is Resource.Loading -> { /* handled by showLoading */
                    }
                }
                showLoading.value = false
            }
        }
    }

    // Validation
    fun validateName() {
        val isValid = name.value.trim().length >= 2
        nameError.value = !isValid
    }

    fun validateEmail() {
        val isValid = email.value.trim().isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email.value.trim()).matches()
        emailError.value = !isValid
    }

    fun validatePassword() {
        val isValid = password.value.trim().length >= 6
        passwordError.value = !isValid
    }

    fun validateConfirmPassword() {
        val isValid = password.value.trim() == confirmPassword.value.trim() &&
                confirmPassword.value.trim().isNotEmpty()
        confirmPasswordError.value = !isValid
    }

    private fun validateAll() {
        validateName()
        validateEmail()
        validatePassword()
        validateConfirmPassword()
    }

    // Helpers
    fun dismissSnackbar() {
        errorToastMessage.value = ""
    }
}