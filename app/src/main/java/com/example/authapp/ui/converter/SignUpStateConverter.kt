package com.example.authapp.ui.converter


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.authapp.ui.screen.signup.SignUpFormState
import com.example.authapp.ui.screen.signup.SignUpScreenState

@Composable
fun SignUpViewModel.toFormState(): SignUpFormState {
    return SignUpFormState(
        name = name.collectAsState(),
        nameError = nameError.collectAsState(),
        onNameChanged = ::onNameChanged,
        onNameFocusChanged = ::onNameFocusLost,

        email = email.collectAsState(),
        emailError = emailError.collectAsState(),
        onEmailChanged = ::onEmailChanged,
        onEmailFocusChanged = ::onEmailFocusLost,

        password = password.collectAsState(),
        passwordError = passwordError.collectAsState(),
        onPasswordChanged = ::onPasswordChanged,
        onPasswordFocusChanged = ::onPasswordFocusLost,

        confirmPassword = confirmPassword.collectAsState(),
        confirmPasswordError = confirmPasswordError.collectAsState(),
        onConfirmPasswordChanged = ::onConfirmPasswordChanged,
        onConfirmPasswordFocusChanged = ::onConfirmPasswordFocusLost,

        onSignUp = ::signUp,
        signUpEnabled = signUpEnabled.collectAsState(),
        errorToastMessage = errorToastMessage.collectAsState(),
        dismissSnackbar = ::dismissSnackbar
    )
}

@Composable
fun SignUpViewModel.toScreenState(
    onNavigateToSignIn: () -> Unit
): SignUpScreenState {
    return SignUpScreenState(
        formState = toFormState(),
        isLoading = showLoading.collectAsState(),
        onNavigateToSignIn = onNavigateToSignIn,
        onGoogleSignUpResult = ::signUpWithGoogle
    )
}