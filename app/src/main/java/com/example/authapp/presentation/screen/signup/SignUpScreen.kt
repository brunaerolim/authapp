package com.example.authapp.presentation.screen.signup

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.authapp.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Composable
fun SignUpScreen(
    state: SignUpFormState,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()

    // Handle Google Sign Up
    LaunchedEffect(Unit) {
        state.startGoogleSignUp.collect {
            doGoogleSignUp(context, state, coroutineScope)
        }
    }

    LaunchedEffect(state.isLoading.value) {
        if (state.isLoading.value) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                keyboardController?.hide()
                focusManager.clearFocus()
            },
        color = MaterialTheme.colorScheme.surface
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val (
                logo, title, subtitle, nameField, nameError,
                emailField, emailError, passwordField, passwordError,
                confirmPasswordField, confirmPasswordError, termsRow,
                signUpButton, divider, googleButton, signInRow
            ) = createRefs()

            val errorCard = createRef()

            // App Logo
            AppLogo(
                modifier = Modifier.constrainAs(logo) {
                    top.linkTo(parent.top, margin = 32.dp)
                    centerHorizontallyTo(parent)
                }
            )

            // Title
            Text(
                text = stringResource(R.string.create_account_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(logo.bottom, margin = 24.dp)
                    centerHorizontallyTo(parent)
                }
            )

            // Subtitle
            Text(
                text = stringResource(R.string.create_account_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(subtitle) {
                    top.linkTo(title.bottom, margin = 8.dp)
                    centerHorizontallyTo(parent)
                }
            )

            // Name Field
            NameField(
                value = state.name.value,
                onValueChange = state.onNameChanged,
                onFocusLost = state.onNameFocusChanged,
                isError = state.nameError.value,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                modifier = Modifier.constrainAs(nameField) {
                    top.linkTo(subtitle.bottom, margin = 40.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Name Error
            if (state.nameError.value) {
                ErrorText(
                    text = stringResource(R.string.name_error_message),
                    modifier = Modifier.constrainAs(nameError) {
                        top.linkTo(nameField.bottom, margin = 4.dp)
                        start.linkTo(nameField.start, margin = 16.dp)
                        end.linkTo(nameField.end)
                        width = Dimension.fillToConstraints
                    }
                )
            }

            // Email Field
            EmailField(
                value = state.email.value,
                onValueChange = state.onEmailChanged,
                onFocusLost = state.onEmailFocusChanged,
                isError = state.emailError.value,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                modifier = Modifier.constrainAs(emailField) {
                    top.linkTo(
                        if (state.nameError.value) nameError.bottom else nameField.bottom,
                        margin = 16.dp
                    )
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Email Error
            if (state.emailError.value) {
                ErrorText(
                    text = stringResource(R.string.email_invalid_error),
                    modifier = Modifier.constrainAs(emailError) {
                        top.linkTo(emailField.bottom, margin = 4.dp)
                        start.linkTo(emailField.start, margin = 16.dp)
                        end.linkTo(emailField.end)
                        width = Dimension.fillToConstraints
                    }
                )
            }

            // Password Field
            PasswordField(
                value = state.password.value,
                onValueChange = state.onPasswordChanged,
                onFocusLost = state.onPasswordFocusChanged,
                isVisible = state.isPasswordVisible.value,
                onToggleVisibility = state.onTogglePasswordVisibility,
                isError = state.passwordError.value,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                label = stringResource(R.string.password_label),
                modifier = Modifier.constrainAs(passwordField) {
                    top.linkTo(
                        if (state.emailError.value) emailError.bottom else emailField.bottom,
                        margin = 16.dp
                    )
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Password Error
            if (state.passwordError.value) {
                ErrorText(
                    text = stringResource(R.string.password_error_message),
                    modifier = Modifier.constrainAs(passwordError) {
                        top.linkTo(passwordField.bottom, margin = 4.dp)
                        start.linkTo(passwordField.start, margin = 16.dp)
                        end.linkTo(passwordField.end)
                        width = Dimension.fillToConstraints
                    }
                )
            }

            // Confirm Password Field
            PasswordField(
                value = state.confirmPassword.value,
                onValueChange = state.onConfirmPasswordChanged,
                onFocusLost = state.onConfirmPasswordFocusChanged,
                isVisible = state.isConfirmPasswordVisible.value,
                onToggleVisibility = state.onToggleConfirmPasswordVisibility,
                isError = state.confirmPasswordError.value,
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (state.signUpEnabled.value) state.onSignUp()
                },
                label = stringResource(R.string.confirm_password_label),
                modifier = Modifier.constrainAs(confirmPasswordField) {
                    top.linkTo(
                        if (state.passwordError.value) passwordError.bottom else passwordField.bottom,
                        margin = 16.dp
                    )
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Confirm Password Error
            if (state.confirmPasswordError.value) {
                ErrorText(
                    text = stringResource(R.string.passwords_not_match_error),
                    modifier = Modifier.constrainAs(confirmPasswordError) {
                        top.linkTo(confirmPasswordField.bottom, margin = 4.dp)
                        start.linkTo(confirmPasswordField.start, margin = 16.dp)
                        end.linkTo(confirmPasswordField.end)
                        width = Dimension.fillToConstraints
                    }
                )
            }

            // Terms Checkbox
            TermsRow(
                isChecked = state.acceptTerms.value,
                onCheckedChange = state.onAcceptTermsChanged,
                modifier = Modifier.constrainAs(termsRow) {
                    top.linkTo(
                        if (state.confirmPasswordError.value) confirmPasswordError.bottom else confirmPasswordField.bottom,
                        margin = 16.dp
                    )
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Sign Up Button
            PrimaryButton(
                text = stringResource(R.string.create_account_button),
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    state.onSignUp()
                },
                enabled = state.signUpEnabled.value && !state.isLoading.value,
                isLoading = state.isLoading.value,
                modifier = Modifier.constrainAs(signUpButton) {
                    top.linkTo(termsRow.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Divider
            DividerWithText(
                text = stringResource(R.string.or_divider),
                modifier = Modifier.constrainAs(divider) {
                    top.linkTo(signUpButton.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Google Sign Up Button
            GoogleSignUpButton(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    state.onGoogleSignUp()
                },
                enabled = !state.isLoading.value,
                modifier = Modifier.constrainAs(googleButton) {
                    top.linkTo(divider.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Sign In Row
            SignInRow(
                onNavigateToSignIn = state.onNavigateToSignIn,
                enabled = !state.isLoading.value,
                modifier = Modifier.constrainAs(signInRow) {
                    top.linkTo(googleButton.bottom, margin = 32.dp)
                    centerHorizontallyTo(parent)
                }
            )

            // Error Card
            if (state.errorToastMessage.value.isNotEmpty()) {
                ErrorCard(
                    message = state.errorToastMessage.value,
                    modifier = Modifier.constrainAs(errorCard) {
                        top.linkTo(signInRow.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        bottom.linkTo(parent.bottom, margin = 24.dp)
                    }
                )
            }
        }
    }
}

private fun doGoogleSignUp(
    context: Context,
    state: SignUpFormState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    val credentialManager = CredentialManager.create(context)

    val rawNonce = UUID.randomUUID().toString()
    val bytes = rawNonce.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    val hashedNonce = digest.fold("") { str, it ->
        str + "%02x".format(it)
    }

    val googleSignInOption =
        GetSignInWithGoogleOption.Builder(context.getString(R.string.default_web_client_id))
            .setNonce(hashedNonce)
            .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleSignInOption)
        .build()

    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            handleGoogleSignUpResult(result, state)
        } catch (e: NoCredentialException) {
            Log.e("SignUp", "No credential available", e)
            state.onGoogleSignUpError("No Google account found")
        } catch (e: GetCredentialException) {
            Log.e("SignUp", "Get credential exception", e)
            state.onGoogleSignUpError("Google Sign Up failed: ${e.message}")
        }
    }
}

private fun handleGoogleSignUpResult(
    result: GetCredentialResponse,
    state: SignUpFormState
) {
    when (val credential = result.credential) {
        is androidx.credentials.CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    state.onGoogleSignUpResult(result)
                } catch (e: Exception) {
                    Log.e("SignUp", "Failed to get Google ID token", e)
                    state.onGoogleSignUpError("Failed to process Google credentials")
                }
            } else {
                Log.e("SignUp", "Unexpected credential type")
                state.onGoogleSignUpError("Invalid credential type")
            }
        }

        else -> {
            Log.e("SignUp", "Unexpected credential type")
            state.onGoogleSignUpError("Invalid credential")
        }
    }
}

@Composable
private fun AppLogo(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.ic_launcher),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        modifier = modifier.size(48.dp)
    )
}

@Composable
private fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: (Boolean) -> Unit,
    isError: Boolean,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                stringResource(R.string.full_name_label),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        modifier = modifier.onFocusChanged { focusState ->
            if (!focusState.isFocused) onFocusLost(true)
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = createTextFieldColors(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
private fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: (Boolean) -> Unit,
    isError: Boolean,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                stringResource(R.string.email_label),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        modifier = modifier.onFocusChanged { focusState ->
            if (!focusState.isFocused) onFocusLost(true)
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = createTextFieldColors(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: (Boolean) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        visualTransformation = if (isVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = if (onDone != null) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext?.invoke() },
            onDone = { onDone?.invoke() }
        ),
        modifier = modifier.onFocusChanged { focusState ->
            if (!focusState.isFocused) onFocusLost(true)
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = createTextFieldColors(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) {
                        Icons.Filled.VisibilityOff
                    } else {
                        Icons.Filled.Visibility
                    },
                    contentDescription = if (isVisible) stringResource(R.string.hide_password) else stringResource(
                        R.string.show_password
                    ),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun TermsRow(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.accept_terms_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun DividerWithText(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
        Text(
            text = "  $text  ",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun GoogleSignUpButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = "Google Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                stringResource(R.string.google_signup_button),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun SignInRow(
    onNavigateToSignIn: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.already_have_account),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )
        TextButton(
            onClick = onNavigateToSignIn,
            enabled = enabled
        ) {
            Text(
                text = stringResource(R.string.sign_in_link),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun ErrorText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
    )
}

@Composable
private fun ErrorCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun createTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    cursorColor = MaterialTheme.colorScheme.primary,
    errorBorderColor = MaterialTheme.colorScheme.error,
    errorLabelColor = MaterialTheme.colorScheme.error
)