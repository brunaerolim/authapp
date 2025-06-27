package com.example.authapp.domain.usecase.payment

import com.example.authapp.core.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ValidateCardUseCase @Inject constructor() {
    suspend operator fun invoke(
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvc: String,
        cardHolderName: String
    ): Resource<String> = withContext(Dispatchers.IO) {
        try {
            if (cardNumber.isNotBlank() &&
                expiryMonth in 1..12 &&
                expiryYear >= 2024 &&
                cvc.length in 3..4 &&
                cardHolderName.isNotBlank()
            ) {
                Resource.Success("Local validation passed")
            } else {
                Resource.Failure(Exception("Invalid card data"))
            }
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }
}