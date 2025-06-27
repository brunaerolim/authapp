package com.example.authapp.presentation.screen.cardvalidation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.authapp.R
import com.example.authapp.core.utils.Resource
import com.example.authapp.presentation.theme.PastelPink
import com.example.authapp.presentation.theme.PastelPinkDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardValidationScreen(
    state: CardValidationState,
    onValidationSuccess: () -> Unit = {},
    onValidationError: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    val cardNumberFocusRequester = remember { FocusRequester() }
    val expiryDateFocusRequester = remember { FocusRequester() }
    val cvcFocusRequester = remember { FocusRequester() }
    val cardHolderNameFocusRequester = remember { FocusRequester() }

    var isCvcVisible by rememberSaveable { mutableStateOf(false) }

    // Handle validation results
    val currentValidationResult = state.validationResult.value
    val isProcessing = state.isProcessing.value

    when (currentValidationResult) {
        is Resource.Success -> {
            keyboardController?.hide()
            onValidationSuccess()
            state.onValidationResultHandled()
        }

        is Resource.Failure -> {
            val errorMessage = currentValidationResult.throwable.message
                ?: stringResource(R.string.validation_error)
            onValidationError(errorMessage)
            state.onValidationResultHandled()
        }

        is Resource.Loading -> {
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            state.onBack()
                        },
                        modifier = Modifier.semantics {
                            role = Role.Button
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = stringResource(R.string.back_button_description)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
        ) {
            val (content, loadingOverlay) = createRefs()

            Column(
                modifier = Modifier
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // Card Icon
                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.CenterHorizontally)
                        .semantics {},
                    colors = CardDefaults.cardColors(containerColor = PastelPink)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = PastelPinkDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.card_validation_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Card Number Field
                OutlinedTextField(
                    value = state.cardNumber.value,
                    onValueChange = state.onCardNumberChange,
                    label = { Text(stringResource(R.string.card_number)) },
                    placeholder = { Text("1234 5678 9012 3456") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            expiryDateFocusRequester.requestFocus()
                        }
                    ),
                    isError = state.cardNumberError.value.isNotEmpty(),
                    supportingText = if (state.cardNumberError.value.isNotEmpty()) {
                        {
                            Text(
                                text = state.cardNumberError.value,
                                modifier = Modifier.semantics {}
                            )
                        }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(cardNumberFocusRequester)
                        .onFocusChanged { focusState ->
                            state.onCardNumberFocusChanged(focusState.hasFocus)
                        },
                    singleLine = true,
                    enabled = !isProcessing
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Expiry Date and CVC Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.expiryDate.value,
                        onValueChange = state.onExpiryDateChange,
                        label = { Text(stringResource(R.string.expiry_date)) },
                        placeholder = { Text("MM/YY") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                cvcFocusRequester.requestFocus()
                            }
                        ),
                        isError = state.expiryDateError.value.isNotEmpty(),
                        supportingText = if (state.expiryDateError.value.isNotEmpty()) {
                            {
                                Text(
                                    text = state.expiryDateError.value,
                                    modifier = Modifier.semantics {}
                                )
                            }
                        } else null,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(expiryDateFocusRequester)
                            .onFocusChanged { focusState ->
                                state.onExpiryDateFocusChanged(focusState.hasFocus)
                            }
                            .semantics {
                                if (state.expiryDateError.value.isNotEmpty()) {
                                    error(state.expiryDateError.value)
                                }
                            },
                        singleLine = true,
                        enabled = !isProcessing
                    )

                    OutlinedTextField(
                        value = state.cvc.value,
                        onValueChange = state.onCvcChange,
                        label = { Text(stringResource(R.string.cvc)) },
                        placeholder = { Text("123") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                cardHolderNameFocusRequester.requestFocus()
                            }
                        ),
                        visualTransformation = if (isCvcVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { isCvcVisible = !isCvcVisible },
                                modifier = Modifier
                            ) {
                                Icon(
                                    imageVector = if (isCvcVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = null
                                )
                            }
                        },
                        isError = state.cvcError.value.isNotEmpty(),
                        supportingText = if (state.cvcError.value.isNotEmpty()) {
                            {
                                Text(
                                    text = state.cvcError.value,
                                    modifier = Modifier.semantics {}
                                )
                            }
                        } else null,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(cvcFocusRequester)
                            .onFocusChanged { focusState ->
                                state.onCvcFocusChanged(focusState.hasFocus)
                            },
                        singleLine = true,
                        enabled = !isProcessing
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Holder Name Field
                OutlinedTextField(
                    value = state.cardHolderName.value,
                    onValueChange = state.onCardHolderNameChange,
                    label = { Text(stringResource(R.string.card_holder_name)) },
                    placeholder = { Text(stringResource(R.string.card_holder_name_placeholder)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    ),
                    isError = state.cardHolderNameError.value.isNotEmpty(),
                    supportingText = if (state.cardHolderNameError.value.isNotEmpty()) {
                        {
                            Text(
                                text = state.cardHolderNameError.value,
                                modifier = Modifier.semantics {}
                            )
                        }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(cardHolderNameFocusRequester)
                        .onFocusChanged { focusState ->
                            state.onCardHolderNameFocusChanged(focusState.hasFocus)
                        }
                        .semantics {
                            if (state.cardHolderNameError.value.isNotEmpty()) {
                                error(state.cardHolderNameError.value)
                            }
                        },
                    singleLine = true,
                    enabled = !isProcessing
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Validate Button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        state.onValidateCard()
                    },
                    enabled = state.isFormValid.value && !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isProcessing) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.validating_card),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.validate_card),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Security Information Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {},
                    colors = CardDefaults.cardColors(
                        containerColor = PastelPink.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PastelPinkDark,
                            modifier = Modifier.padding(end = 12.dp, top = 2.dp)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.security_info_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.security_info_description),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Loading Overlay
            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .constrainAs(loadingOverlay) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            // Consume clicks to prevent interaction with underlying content
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.validating_card_message),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}