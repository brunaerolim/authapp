package com.example.authapp.presentation.screen.cardvalidation

import androidx.compose.runtime.State
import com.example.authapp.core.utils.Resource

data class CardValidationState(
    val cardNumber: State<String>,
    val expiryDate: State<String>,
    val cvc: State<String>,
    val cardHolderName: State<String>,
    val isCardNumberValid: State<Boolean>,
    val isExpiryDateValid: State<Boolean>,
    val isCvcValid: State<Boolean>,
    val isCardHolderNameValid: State<Boolean>,
    val validationResult: State<Resource<String>>,
    val isFormValid: State<Boolean>,
    val isProcessing: State<Boolean>,
    val cardNumberError: State<String>,
    val expiryDateError: State<String>,
    val cvcError: State<String>,
    val cardHolderNameError: State<String>,
    val onCardNumberChange: (String) -> Unit,
    val onExpiryDateChange: (String) -> Unit,
    val onCvcChange: (String) -> Unit,
    val onCardHolderNameChange: (String) -> Unit,
    val onValidateCard: () -> Unit,
    val onBack: () -> Unit,
    val onCardNumberFocusChanged: (Boolean) -> Unit,
    val onExpiryDateFocusChanged: (Boolean) -> Unit,
    val onCvcFocusChanged: (Boolean) -> Unit,
    val onCardHolderNameFocusChanged: (Boolean) -> Unit,
    val onValidationResultHandled: () -> Unit
)
