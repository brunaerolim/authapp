package com.example.authapp.presentation.viewmodel.cardvalidation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.authapp.presentation.screen.cardvalidation.CardValidationState

@Composable
fun CardValidationViewModel.toScreenState(
    onBack: () -> Unit
): CardValidationState = CardValidationState(
    cardNumber = cardNumber.collectAsState(),
    expiryDate = expiryDate.collectAsState(),
    cvc = cvc.collectAsState(),
    cardHolderName = cardHolderName.collectAsState(),
    isCardNumberValid = isCardNumberValid.collectAsState(),
    isExpiryDateValid = isExpiryDateValid.collectAsState(),
    isCvcValid = isCvcValid.collectAsState(),
    isCardHolderNameValid = isCardHolderNameValid.collectAsState(),
    validationResult = validationResult.collectAsState(),
    isFormValid = isFormValid.collectAsState(),
    isProcessing = isProcessing.collectAsState(),
    cardNumberError = cardNumberError.collectAsState(),
    expiryDateError = expiryDateError.collectAsState(),
    cvcError = cvcError.collectAsState(),
    cardHolderNameError = cardHolderNameError.collectAsState(),
    onCardNumberChange = ::onCardNumberChange,
    onExpiryDateChange = ::onExpiryDateChange,
    onCvcChange = ::onCvcChange,
    onCardHolderNameChange = ::onCardHolderNameChange,
    onValidateCard = ::validateCard,
    onBack = onBack,
    onCardNumberFocusChanged = ::onCardNumberFocusChanged,
    onExpiryDateFocusChanged = ::onExpiryDateFocusChanged,
    onCvcFocusChanged = ::onCvcFocusChanged,
    onCardHolderNameFocusChanged = ::onCardHolderNameFocusChanged,
    onValidationResultHandled = ::onValidationResultHandled
)