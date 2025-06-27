package com.example.authapp.presentation.viewmodel.cardvalidation.payment.failure

import androidx.lifecycle.ViewModel
import com.example.authapp.core.resources.ResourceProvider
import com.example.authapp.presentation.screen.cardvalidation.failure.PaymentFailureState
import org.koin.core.component.KoinComponent

class PaymentFailureViewModel(
    private val resourceProvider: ResourceProvider
) : ViewModel(), KoinComponent {

    private var errorMessage: String = ""
    private var errorThrowable: Throwable? = null

    fun setError(errorMessage: String, errorThrowable: Throwable? = null) {
        this.errorMessage = errorMessage
        this.errorThrowable = errorThrowable
    }

    fun getState(
        onTryAgain: () -> Unit,
        onBackToHome: () -> Unit
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
}