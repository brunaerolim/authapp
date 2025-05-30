package com.example.authapp.ui.screen.signin

import androidx.compose.runtime.State
import com.google.firebase.auth.AuthCredential

data class SignInFormState(
    val email: State<String>,
    val emailError: State<Boolean>,
    val onEmailChanged: (String) -> Unit,
    val onEmailFocusLost: () -> Unit,
    val password: State<String>,
    val passwordError: State<Boolean>,
    val onPasswordChanged: (String) -> Unit,
    val onPasswordFocusLost: () -> Unit,
    val onSignIn: () -> Unit,
    val onGoogleSignIn: () -> Unit,
    val errorMessage: State<String>,
    val onDismissError: () -> Unit,
    val isSignInEnabled: State<Boolean>
)

data class SignInScreenState(
    val formState: SignInFormState,
    val isLoading: State<Boolean>,
    val onNavigateToSignUp: () -> Unit,
    val onGoogleSignInResult: (AuthCredential) -> Unit
)