package com.example.authapp.presentation.screen.signin

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.authapp.R
import com.example.authapp.presentation.theme.PastelSurface
import com.example.authapp.presentation.theme.Pink40
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Composable
fun SignInScreen(
    state: SignInFormState
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        state.startGoogleSignIn.collect {
            doGoogleSignIn(context, state, coroutineScope)
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
        color = PastelSurface
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val (
                logo, title, subtitle, emailField, emailError,
                passwordField, passwordError, rememberMeRow,
                signInButton, forgotPasswordLink, divider,
                googleButton, signUpRow, errorCard
            ) = createRefs()

            // App Logo
            AppLogo(
                modifier = Modifier.constrainAs(logo) {
                    top.linkTo(parent.top, margin = 32.dp)
                    centerHorizontallyTo(parent)
                }
            )

            // Title
            Text(
                text = stringResource(R.string.sign_in_welcome_back),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Pink40
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(logo.bottom, margin = 24.dp)
                    centerHorizontallyTo(parent)
                }
            )

            // Subtitle
            Text(
                text = stringResource(R.string.sign_in_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Pink40
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(subtitle) {
                    top.linkTo(title.bottom, margin = 8.dp)
                    centerHorizontallyTo(parent)
                }
            )

            // Email Field
            EmailField(
                value = state.email.value,
                onValueChange = state.onEmailChanged,
                onFocusLost = state.onEmailFocusLost,
                isError = state.emailError.value,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                modifier = Modifier.constrainAs(emailField) {
                    top.linkTo(subtitle.bottom, margin = 40.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Email Error
            if (state.emailError.value) {
                ErrorText(
                    text = if (state.email.value.trim().isEmpty()) {
                        stringResource(R.string.sign_in_email_required_error)
                    } else {
                        stringResource(R.string.sign_in_email_invalid_error)
                    },
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
                onFocusLost = state.onPasswordFocusLost,
                isVisible = state.isPasswordVisible.value,
                onToggleVisibility = state.onTogglePasswordVisibility,
                isError = state.passwordError.value,
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (state.isSignInEnabled.value) state.onSignIn()
                },
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
                    text = if (state.password.value.trim().isEmpty()) {
                        stringResource(R.string.sign_in_password_required_error)
                    } else {
                        stringResource(R.string.sign_in_password_length_error)
                    },
                    modifier = Modifier.constrainAs(passwordError) {
                        top.linkTo(passwordField.bottom, margin = 4.dp)
                        start.linkTo(passwordField.start, margin = 16.dp)
                        end.linkTo(passwordField.end)
                        width = Dimension.fillToConstraints
                    }
                )
            }

            // Remember Me Row
            RememberMeRow(
                isChecked = state.rememberMe.value,
                onCheckedChange = state.onRememberMeChanged,
                modifier = Modifier.constrainAs(rememberMeRow) {
                    top.linkTo(
                        if (state.passwordError.value) passwordError.bottom else passwordField.bottom,
                        margin = 16.dp
                    )
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Sign In Button
            PrimaryButton(
                text = stringResource(R.string.sign_in_button),
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    state.onSignIn()
                },
                enabled = state.isSignInEnabled.value && !state.isLoading.value,
                isLoading = state.isLoading.value,
                modifier = Modifier.constrainAs(signInButton) {
                    top.linkTo(rememberMeRow.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Forgot Password Link
            Text(
                text = AnnotatedString(stringResource(R.string.sign_in_forgot_password)),
                style = TextStyle(
                    color = Pink40,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .clickable {
                        state.onNavigateToForgotPassword()
                    }
                    .constrainAs(forgotPasswordLink) {
                        top.linkTo(signInButton.bottom, margin = 24.dp)
                        centerHorizontallyTo(parent)
                    }
            )

            // Divider
            DividerWithText(
                text = stringResource(R.string.sign_in_or_divider),
                modifier = Modifier.constrainAs(divider) {
                    top.linkTo(forgotPasswordLink.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Google Sign In Button
            GoogleSignInButton(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    state.onGoogleSignIn()
                },
                enabled = !state.isLoading.value,
                modifier = Modifier.constrainAs(googleButton) {
                    top.linkTo(divider.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            // Sign Up Row
            SignUpRow(
                onNavigateToSignUp = state.onNavigateToSignUp,
                enabled = !state.isLoading.value,
                modifier = Modifier.constrainAs(signUpRow) {
                    top.linkTo(googleButton.bottom, margin = 32.dp)
                    centerHorizontallyTo(parent)
                }
            )

            if (state.errorMessage.value.isNotEmpty()) {
                ErrorCard(
                    message = state.errorMessage.value,
                    modifier = Modifier.constrainAs(errorCard) {
                        top.linkTo(signUpRow.bottom, margin = 24.dp)
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

@Composable
private fun AppLogo(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.ic_launcher),
        contentDescription = null,
        tint = Pink40,
        modifier = modifier.size(48.dp)
    )
}

@Composable
private fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit,
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
                color = Pink40.copy(alpha = 0.7f)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        modifier = modifier.onFocusChanged { focusState ->
            if (!focusState.isFocused) onFocusLost()
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = createTextFieldColors(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = Pink40
            )
        }
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    isError: Boolean,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                stringResource(R.string.password_label),
                color = Pink40.copy(alpha = 0.7f)
            )
        },
        visualTransformation = if (isVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier.onFocusChanged { focusState ->
            if (!focusState.isFocused) onFocusLost()
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = createTextFieldColors(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Pink40
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
                    tint = Pink40
                )
            }
        }
    )
}

@Composable
private fun RememberMeRow(
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
                checkedColor = Pink40,
                uncheckedColor = Pink40.copy(alpha = 0.6f),
                checkmarkColor = Pink40
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.sign_in_remember_me),
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
            containerColor = Pink40,
            contentColor = Pink40,
            disabledContainerColor = Pink40.copy(alpha = 0.5f)
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
private fun GoogleSignInButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Pink40
        ),
        border = BorderStroke(1.dp, Pink40),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = "Google Icon",
                tint = Pink40,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                stringResource(R.string.sign_in_continue_with_google),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun SignUpRow(
    onNavigateToSignUp: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.sign_in_dont_have_account),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )
        TextButton(
            onClick = onNavigateToSignUp,
            enabled = enabled
        ) {
            Text(
                text = "Sign Up",
                color = Pink40,
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

private fun doGoogleSignIn(
    context: Context,
    state: SignInFormState,
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
            handleGoogleSignInResult(result, state)
        } catch (e: NoCredentialException) {
            Log.e("SignIn", "No credential available", e)
            state.onGoogleSignInResult(null)
        } catch (e: GetCredentialException) {
            Log.e("SignIn", "Get credential exception", e)
            state.onGoogleSignInResult(null)
        }
    }
}

private fun handleGoogleSignInResult(
    result: GetCredentialResponse,
    state: SignInFormState
) {
    when (val credential = result.credential) {
        is androidx.credentials.CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    state.onGoogleSignInResult(idToken)
                } catch (e: Exception) {
                    Log.e("SignIn", "Failed to get Google ID token", e)
                    state.onGoogleSignInResult(null)
                }
            } else {
                Log.e("SignIn", "Unexpected credential type")
                state.onGoogleSignInResult(null)
            }
        }

        else -> {
            Log.e("SignIn", "Unexpected credential type")
            state.onGoogleSignInResult(null)
        }
    }
}