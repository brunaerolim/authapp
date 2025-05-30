package com.example.authapp.ui.converter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.authapp.ui.screen.signin.SignInFormState
import com.example.authapp.ui.screen.signin.SignInScreenState

@Composable
fun SignInViewModel.toFormState(
    onGoogleSignIn: () -> Unit,
    onGoogleSignInRequest: () -> Unit
): SignInFormState {
    return SignInFormState(
        email = email.collectAsState(),
        emailError = emailError.collectAsState(),
        onEmailChanged = ::updateEmail,
        onEmailFocusLost = ::validateEmail,
        password = password.collectAsState(),
        passwordError = passwordError.collectAsState(),
        onPasswordChanged = ::updatePassword,
        onPasswordFocusLost = ::validatePassword,
        onSignIn = ::signIn,
        onGoogleSignIn = onGoogleSignInRequest,
        errorMessage = errorToastMessage.collectAsState(),
        onDismissError = ::dismissErrorMessage,
        isSignInEnabled = isSignInEnabled.collectAsState()
    )
}

@Composable
fun SignInViewModel.toScreenState(
    onNavigateToSignUp: () -> Unit,
    onGoogleSignIn: () -> Unit
): SignInScreenState {

    return SignInScreenState(
        formState = toFormState(
            onGoogleSignIn = onGoogleSignIn,
            onGoogleSignInRequest = ::requestGoogleSignIn
        ),
        isLoading = showLoading.collectAsState(),
        onNavigateToSignUp = onNavigateToSignUp,
        onGoogleSignInResult = ::signInWithGoogle
    )
}