package com.example.authapp.presentation.screen.signin

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.SharedFlow

data class SignInFormState(
    val email: State<String>,
    val password: State<String>,
    val isPasswordVisible: State<Boolean>,
    val rememberMe: State<Boolean>,
    val isLoading: State<Boolean>,
    val errorMessage: State<String>,
    val toastMessage: State<String>,
    val emailError: State<Boolean>,
    val passwordError: State<Boolean>,
    val isSignInEnabled: State<Boolean>,
    val startGoogleSignIn: SharedFlow<Unit>,
    val onEmailChanged: (String) -> Unit,
    val onPasswordChanged: (String) -> Unit,
    val onTogglePasswordVisibility: () -> Unit,
    val onRememberMeChanged: (Boolean) -> Unit,
    val onSignIn: () -> Unit,
    val onGoogleSignIn: () -> Unit,
    val onGoogleSignInResult: (String?) -> Unit,
    val onNavigateToSignUp: () -> Unit,
    val onEmailFocusLost: () -> Unit,
    val onPasswordFocusLost: () -> Unit,
    val onNavigateToForgotPassword: () -> Unit,
)