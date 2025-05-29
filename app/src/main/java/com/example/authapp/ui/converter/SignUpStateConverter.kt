package com.example.authapp.ui.converter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.authapp.ui.screen.signup.SignUpFormState
import com.example.authapp.ui.screen.signup.SignUpScreenState
import com.example.authapp.utils.setValue

@Composable
fun SignUpViewModel.toFormState() = SignUpFormState(
    name = name.collectAsState(),
    nameError = nameError.collectAsState(),
    onNameChanged = { name.setValue = it },
    onNameFocusChanged = { if (!it) validateName() },
    email = email.collectAsState(),
    emailError = emailError.collectAsState(),
    onEmailChanged = { email.setValue = it },
    onEmailFocusChanged = { if (!it) validateEmail() },
    password = password.collectAsState(),
    passwordError = passwordError.collectAsState(),
    onPasswordChanged = { password.setValue = it },
    onPasswordFocusChanged = { if (!it) validatePassword() },
    confirmPassword = confirmPassword.collectAsState(),
    confirmPasswordError = confirmPasswordError.collectAsState(),
    onConfirmPasswordChanged = { confirmPassword.setValue = it },
    onConfirmPasswordFocusChanged = { if (!it) validateConfirmPassword() },
    onSignUp = ::onSignUp,
    errorToastMessage = errorToastMessage.collectAsState(),
    dismissSnackbar = ::dismissSnackbar,
    signUpEnabled = signUpEnabled.collectAsState()
)

@Composable
fun SignUpViewModel.toScreenState(
    onNavigateToSignIn: () -> Unit
) = SignUpScreenState(
    formState = toFormState(),
    isLoading = showLoading.collectAsState(),
    onNavigateToSignIn = onNavigateToSignIn
)