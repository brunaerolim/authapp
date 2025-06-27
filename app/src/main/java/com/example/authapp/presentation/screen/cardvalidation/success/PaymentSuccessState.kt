package com.example.authapp.presentation.screen.cardvalidation.success

data class PaymentSuccessState(
    val onContinue: () -> Unit,
    val onValidateAnother: () -> Unit
)