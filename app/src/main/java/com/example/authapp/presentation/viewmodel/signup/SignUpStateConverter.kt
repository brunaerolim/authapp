package com.example.authapp.presentation.viewmodel.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.authapp.presentation.screen.signup.SignUpFormState

@Composable
fun SignUpViewModel.toScreenState(
    onNavigateToSignIn: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
): SignUpFormState {
    return SignUpFormState(
        name = name.collectAsState(),
        nameError = nameError.collectAsState(),
        email = email.collectAsState(),
        emailError = emailError.collectAsState(),
        password = password.collectAsState(),
        passwordError = passwordError.collectAsState(),
        confirmPassword = confirmPassword.collectAsState(),
        confirmPasswordError = confirmPasswordError.collectAsState(),
        isPasswordVisible = isPasswordVisible.collectAsState(),
        isConfirmPasswordVisible = isConfirmPasswordVisible.collectAsState(),
        acceptTerms = acceptTerms.collectAsState(),
        isLoading = showLoading.collectAsState(),
        errorToastMessage = errorToastMessage.collectAsState(),
        signUpEnabled = signUpEnabled.collectAsState(),
        startGoogleSignIn = startGoogleSignIn,
        onNameChanged = ::onNameChanged,
        onNameFocusChanged = ::onNameFocusLost,
        onEmailChanged = ::onEmailChanged,
        onEmailFocusChanged = ::onEmailFocusLost,
        onPasswordChanged = ::onPasswordChanged,
        onPasswordFocusChanged = ::onPasswordFocusLost,
        onConfirmPasswordChanged = ::onConfirmPasswordChanged,
        onConfirmPasswordFocusChanged = ::onConfirmPasswordFocusLost,
        onTogglePasswordVisibility = ::togglePasswordVisibility,
        onToggleConfirmPasswordVisibility = ::toggleConfirmPasswordVisibility,
        onAcceptTermsChanged = ::onAcceptTermsChanged,
        onSignUp = ::signUp,
        onGoogleSignUp = ::onGoogleSignUp,
        onGoogleSignUpResult = ::handleGoogleSignUpResult,
        onNavigateToSignIn = onNavigateToSignIn,
        dismissSnackbar = ::dismissSnackbar,
        onNavigateBack = onNavigateBack
    )
}