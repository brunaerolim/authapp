package com.example.authapp.data.repository.payment

import com.example.authapp.core.utils.Resource

interface PaymentRepository {
    suspend fun validateCard(
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvc: String,
        cardHolderName: String
    ): Resource<String>
}