package com.example.authapp.presentation.screen.signin.forgot

import androidx.compose.runtime.State

data class ForgotPasswordState(
    val email: State<String>,
    val isLoading: State<Boolean>,
    val errorMessage: State<String>,
    val successMessage: State<String>,
    val emailError: State<Boolean>,
    val isSendEmailEnabled: State<Boolean>,
    val onEmailChanged: (String) -> Unit,
    val onSendEmail: () -> Unit,
    val onEmailFocusLost: () -> Unit,
    val onNavigateToResetPassword: (String) -> Unit,
    val onNavigateBack: () -> Unit,
    val onClearMessages: () -> Unit
)