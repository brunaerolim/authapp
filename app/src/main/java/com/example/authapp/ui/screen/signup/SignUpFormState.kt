package com.example.authapp.ui.screen.signup

import androidx.compose.runtime.State

data class SignUpFormState(
    val name: State<String>,
    val nameError: State<Boolean>,
    val onNameChanged: (String) -> Unit,
    val onNameFocusChanged: (Boolean) -> Unit,
    val email: State<String>,
    val emailError: State<Boolean>,
    val onEmailChanged: (String) -> Unit,
    val onEmailFocusChanged: (Boolean) -> Unit,
    val password: State<String>,
    val passwordError: State<Boolean>,
    val onPasswordChanged: (String) -> Unit,
    val onPasswordFocusChanged: (Boolean) -> Unit,
    val confirmPassword: State<String>,
    val confirmPasswordError: State<Boolean>,
    val onConfirmPasswordChanged: (String) -> Unit,
    val onConfirmPasswordFocusChanged: (Boolean) -> Unit,
    val onSignUp: () -> Unit,
    val errorToastMessage: State<String>,
    val dismissSnackbar: () -> Unit,
    val signUpEnabled: State<Boolean>
)

data class SignUpScreenState(
    val formState: SignUpFormState,
    val isLoading: State<Boolean>,
    val onNavigateToSignIn: () -> Unit
)