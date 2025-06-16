package com.example.authapp.presentation.viewmodel.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.authapp.presentation.screen.signin.SignInFormState

@Composable
fun SignInViewModel.toScreenState(
    onNavigateToSignUp: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {}
): SignInFormState {
    return SignInFormState(
        email = email.collectAsState(),
        password = password.collectAsState(),
        isPasswordVisible = isPasswordVisible.collectAsState(),
        rememberMe = rememberMe.collectAsState(),
        isLoading = isLoading.collectAsState(),
        errorMessage = errorMessage.collectAsState(),
        toastMessage = toastMessage.collectAsState(),
        emailError = emailError.collectAsState(),
        passwordError = passwordError.collectAsState(),
        isSignInEnabled = isSignInEnabled.collectAsState(),
        startGoogleSignIn = startGoogleSignIn,
        onEmailChanged = ::onEmailChange,
        onPasswordChanged = ::onPasswordChange,
        onTogglePasswordVisibility = ::onTogglePasswordVisibility,
        onRememberMeChanged = { onToggleRememberMe() },
        onSignIn = ::onSignIn,
        onGoogleSignIn = ::onGoogleSignIn,
        onGoogleSignInResult = ::handleGoogleSignInResult,
        onNavigateToSignUp = onNavigateToSignUp,
        onEmailFocusLost = ::onEmailFocusLost,
        onPasswordFocusLost = ::onPasswordFocusLost,
        onNavigateToForgotPassword = onNavigateToForgotPassword,
    )
}