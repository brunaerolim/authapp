package com.example.authapp.presentation.screen.cardvalidation.failure

data class PaymentFailureState(
    val errorMessage: String = "",
    val errorThrowable: Throwable? = null,
    val onTryAgain: () -> Unit,
    val onBackToHome: () -> Unit
)