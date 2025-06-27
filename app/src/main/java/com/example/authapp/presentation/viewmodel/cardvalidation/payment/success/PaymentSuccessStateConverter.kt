package com.example.authapp.presentation.viewmodel.cardvalidation.payment.success

import androidx.compose.runtime.Composable
import com.example.authapp.presentation.screen.cardvalidation.success.PaymentSuccessState

@Composable
fun paymentSuccessState(
    onContinue: () -> Unit,
    onValidateAnother: () -> Unit
) = PaymentSuccessState(
    onContinue = onContinue,
    onValidateAnother = onValidateAnother
)