package com.example.authapp.presentation.viewmodel.cardvalidation.payment.failure

import androidx.compose.runtime.Composable
import com.example.authapp.core.resources.ResourceProvider
import com.example.authapp.presentation.screen.cardvalidation.failure.PaymentFailureState

@Composable
fun paymentFailureState(
    errorMessage: String,
    errorThrowable: Throwable? = null,
    onTryAgain: () -> Unit,
    onBackToHome: () -> Unit,
    resourceProvider: ResourceProvider
): PaymentFailureState {
    val friendlyMessage = errorMessage.ifBlank {
        errorThrowable?.localizedMessage
            ?: resourceProvider.getString(com.example.authapp.R.string.payment_generic_error)
    }

    return PaymentFailureState(
        errorMessage = friendlyMessage,
        errorThrowable = errorThrowable,
        onTryAgain = onTryAgain,
        onBackToHome = onBackToHome
    )
}