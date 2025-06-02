package com.example.authapp.presentation.screen.signup

import androidx.compose.runtime.State
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.SharedFlow

data class SignUpFormState(
    val name: State<String>,
    val nameError: State<Boolean>,
    val email: State<String>,
    val emailError: State<Boolean>,
    val password: State<String>,
    val passwordError: State<Boolean>,
    val confirmPassword: State<String>,
    val confirmPasswordError: State<Boolean>,
    val isPasswordVisible: State<Boolean>,
    val isConfirmPasswordVisible: State<Boolean>,
    val acceptTerms: State<Boolean>,
    val isLoading: State<Boolean>,
    val errorToastMessage: State<String>,
    val signUpEnabled: State<Boolean>,
    val startGoogleSignIn: SharedFlow<Unit>,
    val onNameChanged: (String) -> Unit,
    val onNameFocusChanged: (Boolean) -> Unit,
    val onEmailChanged: (String) -> Unit,
    val onEmailFocusChanged: (Boolean) -> Unit,
    val onPasswordChanged: (String) -> Unit,
    val onPasswordFocusChanged: (Boolean) -> Unit,
    val onConfirmPasswordChanged: (String) -> Unit,
    val onConfirmPasswordFocusChanged: (Boolean) -> Unit,
    val onTogglePasswordVisibility: () -> Unit,
    val onToggleConfirmPasswordVisibility: () -> Unit,
    val onAcceptTermsChanged: (Boolean) -> Unit,
    val onSignUp: () -> Unit,
    val onGoogleSignUp: () -> Unit,
    val onGoogleSignUpResult: (GoogleSignInAccount?) -> Unit,
    val onNavigateToSignIn: () -> Unit,
    val dismissSnackbar: () -> Unit
)