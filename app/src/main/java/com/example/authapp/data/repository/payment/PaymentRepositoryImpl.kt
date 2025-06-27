package com.example.authapp.data.repository.payment

import com.example.authapp.core.utils.Resource
import com.example.authapp.domain.usecase.payment.ValidateCardUseCase
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val validateCardUseCase: ValidateCardUseCase
) : PaymentRepository {

    override suspend fun validateCard(
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvc: String,
        cardHolderName: String
    ): Resource<String> {
        return validateCardUseCase(cardNumber, expiryMonth, expiryYear, cvc, cardHolderName)
    }
}