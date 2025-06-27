package com.example.authapp.presentation.viewmodel.signin.resetpassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.authapp.presentation.screen.signin.forgot.ForgotPasswordState


@Composable
fun ForgotPasswordViewModel.toForgotScreenState(
    onNavigateToResetPassword: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
): ForgotPasswordState {
    return ForgotPasswordState(
        email = email.collectAsState(),
        isLoading = isLoading.collectAsState(),
        errorMessage = errorToastMessage.collectAsState(),
        successMessage = successToastMessage.collectAsState(),
        emailError = emailError.collectAsState(),
        isSendEmailEnabled = sendEnabled.collectAsState(),
        onEmailChanged = ::onEmailChange,
        onSendEmail = ::sendPasswordResetEmail,
        onEmailFocusLost = ::onEmailFocusLost,
        onNavigateToResetPassword = onNavigateToResetPassword,
        onNavigateBack = onNavigateBack,
        onClearMessages = ::clearMessages
    )
}