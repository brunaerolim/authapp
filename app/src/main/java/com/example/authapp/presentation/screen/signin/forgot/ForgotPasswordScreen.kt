package com.example.authapp.presentation.screen.signin.forgot

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.authapp.R
import com.example.authapp.presentation.theme.Pink40

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Handle snackbar messages
    HandleSnackbarMessages(
        snackbarHostState = snackbarHostState,
        successMessage = state.successMessage.value,
        errorMessage = state.errorMessage.value
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ForgotPasswordTopBar(onNavigateBack = state.onNavigateBack)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        ForgotPasswordContent(
            state = state,
            paddingValues = paddingValues,
            keyboardController = keyboardController,
            focusManager = focusManager
        )
    }
}

@Composable
private fun HandleSnackbarMessages(
    snackbarHostState: SnackbarHostState,
    successMessage: String?,
    errorMessage: String?
) {
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true
            )
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForgotPasswordTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.back_button_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun ForgotPasswordContent(
    state: ForgotPasswordState,
    paddingValues: PaddingValues,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
    ) {
        val (header, form, button) = createRefs()

        // Header Section
        ForgotPasswordHeader(
            modifier = Modifier.constrainAs(header) {
                top.linkTo(parent.top, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        // Form Section
        ForgotPasswordForm(
            state = state,
            keyboardController = keyboardController,
            modifier = Modifier.constrainAs(form) {
                top.linkTo(header.bottom, margin = 40.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        // Button Section
        ForgotPasswordButton(
            state = state,
            keyboardController = keyboardController,
            focusManager = focusManager,
            modifier = Modifier.constrainAs(button) {
                top.linkTo(form.bottom, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}

@Composable
private fun ForgotPasswordHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.forgot_password_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Pink40
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.forgot_password_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Pink40
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ForgotPasswordForm(
    state: ForgotPasswordState,
    keyboardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        EmailTextField(
            value = state.email.value,
            onValueChange = state.onEmailChanged,
            onFocusLost = state.onEmailFocusLost,
            isError = state.emailError.value,
            keyboardController = keyboardController,
            modifier = Modifier.fillMaxWidth()
        )

        if (state.emailError.value) {
            EmailErrorText(
                email = state.email.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit,
    isError: Boolean,
    keyboardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = stringResource(R.string.email_label),
                color = Pink40.copy(alpha = 0.7f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = Pink40
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                onFocusLost()
            }
        ),
        modifier = modifier.onFocusChanged { focusState ->
            if (!focusState.isFocused) {
                onFocusLost()
            }
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Pink40,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = Pink40,
            cursorColor = Pink40,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error
        )
    )
}

@Composable
private fun EmailErrorText(
    email: String,
    modifier: Modifier = Modifier
) {
    val errorMessage = if (email.trim().isEmpty()) {
        stringResource(R.string.email_required_error)
    } else {
        stringResource(R.string.email_invalid_error)
    }

    Text(
        text = errorMessage,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
    )
}

@Composable
private fun ForgotPasswordButton(
    state: ForgotPasswordState,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            keyboardController?.hide()
            focusManager.clearFocus()
            state.onSendEmail()
        },
        modifier = modifier.height(56.dp),
        enabled = state.isSendEmailEnabled.value && !state.isLoading.value,
        colors = ButtonDefaults.buttonColors(
            containerColor = Pink40,
            contentColor = Pink40,
            disabledContainerColor = Pink40.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (state.isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Pink40,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.send_reset_link_button),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}