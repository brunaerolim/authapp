package com.example.authapp.presentation.viewmodel.cardvalidation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.R
import com.example.authapp.core.resources.ResourceProvider
import com.example.authapp.core.utils.Resource
import com.example.authapp.domain.usecase.payment.ValidateCardUseCase
import com.stripe.android.Stripe
import com.stripe.android.model.CardParams
import com.stripe.android.model.Token
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardValidationViewModel(
    private val stripe: Stripe,
    private val validateCardUseCase: ValidateCardUseCase,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _cardNumber = MutableStateFlow("")
    val cardNumber: StateFlow<String> = _cardNumber.asStateFlow()

    private val _expiryDate = MutableStateFlow("")
    val expiryDate: StateFlow<String> = _expiryDate.asStateFlow()

    private val _cvc = MutableStateFlow("")
    val cvc: StateFlow<String> = _cvc.asStateFlow()

    private val _cardHolderName = MutableStateFlow("")
    val cardHolderName: StateFlow<String> = _cardHolderName.asStateFlow()

    private val _isCardNumberValid = MutableStateFlow(false)
    val isCardNumberValid: StateFlow<Boolean> = _isCardNumberValid.asStateFlow()

    private val _isExpiryDateValid = MutableStateFlow(false)
    val isExpiryDateValid: StateFlow<Boolean> = _isExpiryDateValid.asStateFlow()

    private val _isCvcValid = MutableStateFlow(false)
    val isCvcValid: StateFlow<Boolean> = _isCvcValid.asStateFlow()

    private val _isCardHolderNameValid = MutableStateFlow(false)
    val isCardHolderNameValid: StateFlow<Boolean> = _isCardHolderNameValid.asStateFlow()

    private val _validationResult = MutableStateFlow<Resource<String>>(Resource.Loading)
    val validationResult: StateFlow<Resource<String>> = _validationResult.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    private val _cardNumberError = MutableStateFlow("")
    val cardNumberError: StateFlow<String> = _cardNumberError.asStateFlow()

    private val _expiryDateError = MutableStateFlow("")
    val expiryDateError: StateFlow<String> = _expiryDateError.asStateFlow()

    private val _cvcError = MutableStateFlow("")
    val cvcError: StateFlow<String> = _cvcError.asStateFlow()

    private val _cardHolderNameError = MutableStateFlow("")
    val cardHolderNameError: StateFlow<String> = _cardHolderNameError.asStateFlow()

    // Track field interaction states
    private val _cardNumberInteracted = MutableStateFlow(false)
    private val _expiryDateInteracted = MutableStateFlow(false)
    private val _cvcInteracted = MutableStateFlow(false)
    private val _cardHolderNameInteracted = MutableStateFlow(false)

    // Track processing state
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    fun onCardNumberChange(value: String) {
        val formatted = formatCardNumber(value)
        _cardNumber.value = formatted
        val valid = validateCardNumber(formatted)
        _isCardNumberValid.value = valid

        // Only show error if field has been interacted with
        if (_cardNumberInteracted.value) {
            _cardNumberError.value = if (valid || formatted.isEmpty()) "" else "Invalid card number"
        }
        updateFormValidity()
    }

    fun onExpiryDateChange(value: String) {
        val formatted = formatExpiryDate(value)
        _expiryDate.value = formatted
        val valid = validateExpiryDate(formatted)
        _isExpiryDateValid.value = valid

        // Only show error if field has been interacted with
        if (_expiryDateInteracted.value) {
            _expiryDateError.value = if (valid || formatted.isEmpty()) "" else "Invalid expiry date"
        }
        updateFormValidity()
    }

    fun onCvcChange(value: String) {
        if (value.length <= 4) {
            _cvc.value = value
            val valid = validateCvc(value)
            _isCvcValid.value = valid

            // Only show error if field has been interacted with
            if (_cvcInteracted.value) {
                _cvcError.value = if (valid || value.isEmpty()) "" else "Invalid CVC"
            }
            updateFormValidity()
        }
    }

    fun onCardHolderNameChange(value: String) {
        _cardHolderName.value = value
        val valid = value.trim().length >= 2
        _isCardHolderNameValid.value = valid

        // Only show error if field has been interacted with
        if (_cardHolderNameInteracted.value) {
            _cardHolderNameError.value = if (valid || value.isEmpty()) "" else "Name required"
        }
        updateFormValidity()
    }

    // Field focus handlers
    fun onCardNumberFocusChanged(hasFocus: Boolean) {
        if (!hasFocus && !_cardNumberInteracted.value) {
            _cardNumberInteracted.value = true
            val valid = validateCardNumber(_cardNumber.value)
            _cardNumberError.value =
                if (valid || _cardNumber.value.isEmpty()) "" else "Invalid card number"
        }
    }

    fun onExpiryDateFocusChanged(hasFocus: Boolean) {
        if (!hasFocus && !_expiryDateInteracted.value) {
            _expiryDateInteracted.value = true
            val valid = validateExpiryDate(_expiryDate.value)
            _expiryDateError.value =
                if (valid || _expiryDate.value.isEmpty()) "" else "Invalid expiry date"
        }
    }

    fun onCvcFocusChanged(hasFocus: Boolean) {
        if (!hasFocus && !_cvcInteracted.value) {
            _cvcInteracted.value = true
            val valid = validateCvc(_cvc.value)
            _cvcError.value = if (valid || _cvc.value.isEmpty()) "" else "Invalid CVC"
        }
    }

    fun onCardHolderNameFocusChanged(hasFocus: Boolean) {
        if (!hasFocus && !_cardHolderNameInteracted.value) {
            _cardHolderNameInteracted.value = true
            val valid = _cardHolderName.value.trim().length >= 2
            _cardHolderNameError.value =
                if (valid || _cardHolderName.value.isEmpty()) "" else "Name required"
        }
    }

    fun validateCard() {
        if (_isProcessing.value) return

        viewModelScope.launch {
            _isProcessing.value = true
            _validationResult.value = Resource.Loading

            val expiryParts = _expiryDate.value.split("/")
            val month = expiryParts.getOrNull(0)?.toIntOrNull() ?: 0
            val year = expiryParts.getOrNull(1)?.toIntOrNull()?.let { 2000 + it } ?: 0

            // Local validation first
            val localValidation = validateCardUseCase(
                cardNumber = _cardNumber.value.replace(" ", ""),
                expiryMonth = month,
                expiryYear = year,
                cvc = _cvc.value,
                cardHolderName = _cardHolderName.value
            )

            when (localValidation) {
                is Resource.Success -> {
                    tokenizeWithStripe()
                }

                is Resource.Failure -> {
                    _validationResult.value = localValidation
                    _isProcessing.value = false
                }

                is Resource.Loading -> {
                    // Continue loading
                }
            }
        }
    }

    private fun tokenizeWithStripe() {
        val expiryParts = _expiryDate.value.split("/")
        val month = expiryParts.getOrNull(0)?.toIntOrNull() ?: 0
        val year = expiryParts.getOrNull(1)?.toIntOrNull()?.let { 2000 + it } ?: 0

        val cardParams = CardParams(
            number = _cardNumber.value.replace(" ", ""),
            expMonth = month,
            expYear = year,
            cvc = _cvc.value,
            name = _cardHolderName.value
        )

        stripe.createCardToken(
            cardParams = cardParams,
            callback = object : com.stripe.android.ApiResultCallback<Token> {
                override fun onSuccess(result: Token) {
                    _validationResult.value =
                        Resource.Success("Card validated successfully. Token: ${result.id}")
                    _isProcessing.value = false
                }

                override fun onError(e: Exception) {
                    val friendlyMessage = mapStripeExceptionToMessage(e)
                    _validationResult.value = Resource.Failure(Exception(friendlyMessage))
                    _isProcessing.value = false
                }
            }
        )
    }

    private fun mapStripeExceptionToMessage(e: Exception): String {
        return when {
            e.message?.contains("card_declined", ignoreCase = true) == true ->
                resourceProvider.getString(R.string.error_card_declined)

            e.message?.contains("invalid_number", ignoreCase = true) == true ->
                resourceProvider.getString(R.string.error_invalid_number)

            e.message?.contains("expired_card", ignoreCase = true) == true ->
                resourceProvider.getString(R.string.error_expired_card)

            e.message?.contains("incorrect_cvc", ignoreCase = true) == true ->
                resourceProvider.getString(R.string.error_incorrect_cvc)

            else -> resourceProvider.getString(R.string.error_generic_payment)
        }
    }

    fun clearValidationResult() {
        _validationResult.value = Resource.Loading
        _isProcessing.value = false
    }

    fun onValidationResultHandled() {
        // Called when navigation or snackbar is handled
        clearValidationResult()
    }

    private fun updateFormValidity() {
        _isFormValid.value = _isCardNumberValid.value &&
                _isExpiryDateValid.value &&
                _isCvcValid.value &&
                _isCardHolderNameValid.value
    }

    private fun formatCardNumber(input: String): String {
        val digits = input.filter { it.isDigit() }.take(16)
        return digits.chunked(4).joinToString(" ")
    }

    private fun formatExpiryDate(input: String): String {
        val digits = input.filter { it.isDigit() }.take(4)
        return when {
            digits.length <= 2 -> digits
            else -> "${digits.take(2)}/${digits.drop(2)}"
        }
    }

    private fun validateCardNumber(cardNumber: String): Boolean {
        val digits = cardNumber.replace(" ", "")
        return digits.length >= 13 && digits.all { it.isDigit() } && luhnCheck(digits)
    }

    private fun validateExpiryDate(expiryDate: String): Boolean {
        if (!expiryDate.contains("/") || expiryDate.length != 5) return false
        val parts = expiryDate.split("/")
        if (parts.size != 2) return false
        val month = parts[0].toIntOrNull() ?: return false
        val year = parts[1].toIntOrNull() ?: return false
        return month in 1..12 && year >= 24
    }

    private fun validateCvc(cvc: String): Boolean {
        return cvc.length in 3..4 && cvc.all { it.isDigit() }
    }

    private fun luhnCheck(cardNumber: String): Boolean {
        var sum = 0
        var alternate = false
        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber[i].toString().toInt()
            if (alternate) {
                n *= 2
                if (n > 9) n = (n % 10) + 1
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }
}