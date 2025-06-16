package com.example.authapp.presentation.viewmodel.signin.resetpassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.authapp.presentation.screen.signin.forgot.ForgotPasswordState


@Composable
fun ForgotPasswordViewModel.toForgotScreenState(
    onNavigateToResetPassword: (String) -> Unit = {}
): ForgotPasswordState {
    return ForgotPasswordState(
        email = email.collectAsState(),
        isLoading = isLoading.collectAsState(),
        errorMessage = errorMessage.collectAsState(),
        successMessage = successMessage.collectAsState(),
        emailError = emailError.collectAsState(),
        isSendEmailEnabled = isSendEmailEnabled.collectAsState(),
        onEmailChanged = ::onEmailChange,
        onSendEmail = ::sendPasswordResetEmail,
        onEmailFocusLost = ::onEmailFocusLost,
        onNavigateToResetPassword = onNavigateToResetPassword,
        onNavigateBack = ::onNavigateBack,
        onClearMessages = ::clearMessages
    )
}