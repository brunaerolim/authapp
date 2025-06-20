package com.example.authapp.presentation.screen.signin

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
import com.example.authapp.R
import com.example.authapp.presentation.theme.PastelPinkLight
import com.example.authapp.presentation.theme.Pink40
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInScreen(
    state: SignInFormState
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    // Google Sign-In Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleGoogleSignInResult(result, context, state)
    }

    // Google Sign-In Effect
    LaunchedEffect(Unit) {
        state.startGoogleSignIn.collect {
            launchGoogleSignIn(context, googleSignInLauncher)
        }
    }

    // Auto-hide keyboard when loading
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
        color = Color.White
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val (
                logo, title, subtitle, emailField, emailError,
                passwordField, passwordError, rememberMeRow,
                signInButton, forgotPasswordLink, divider,
                googleButton, signUpRow, errorCard
            ) = createRefs()

            // Logo
            AppLogo(
                modifier = Modifier.constrainAs(logo) {
                    top.linkTo(parent.top, margin = 16.dp)
                    centerHorizontallyTo(parent)
                }
            )

            // Title
            Text(
                text = stringResource(R.string.sign_in_welcome_back),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Pink40,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(logo.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                }
            )

            // Subtitle
            Text(
                text = stringResource(R.string.sign_in_subtitle),
                fontSize = 16.sp,
                color = Pink40.copy(alpha = 0.7f),
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
            ClickableText(
                text = AnnotatedString(stringResource(R.string.sign_in_forgot_password)),
                style = TextStyle(
                    color = Pink40,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                ),
                onClick = { state.onNavigateToForgotPassword() },
                modifier = Modifier.constrainAs(forgotPasswordLink) {
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

            // Error Card
            if (state.errorMessage.value.isNotEmpty()) {
                ErrorCard(
                    message = state.errorMessage.value,
                    modifier = Modifier.constrainAs(errorCard) {
                        top.linkTo(signUpRow.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
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
        tint = PastelPinkLight,
        modifier = modifier.size(40.dp)
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
        label = { Text(stringResource(R.string.email_label), color = Pink40.copy(alpha = 0.7f)) },
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
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Pink40,
            unfocusedBorderColor = Pink40.copy(alpha = 0.5f),
            focusedLabelColor = Pink40,
            cursorColor = Pink40,
            errorBorderColor = Color(0xFFE57373),
            errorLabelColor = Color(0xFFE57373)
        ),
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
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Pink40,
            unfocusedBorderColor = Pink40.copy(alpha = 0.5f),
            focusedLabelColor = Pink40,
            cursorColor = Pink40,
            errorBorderColor = Color(0xFFE57373),
            errorLabelColor = Color(0xFFE57373)
        ),
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
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.sign_in_remember_me),
            style = MaterialTheme.typography.bodyMedium,
            color = Pink40.copy(alpha = 0.8f)
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
            contentColor = Color.White,
            disabledContainerColor = Pink40.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
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
        Divider(
            modifier = Modifier.weight(1f),
            color = Pink40.copy(alpha = 0.3f)
        )
        Text(
            text = "  $text  ",
            color = Pink40.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )
        Divider(
            modifier = Modifier.weight(1f),
            color = Pink40.copy(alpha = 0.3f)
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
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                stringResource(R.string.sign_in_continue_with_google),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
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
            color = Pink40.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )
        TextButton(
            onClick = onNavigateToSignUp,
            enabled = enabled
        ) {
            Text(
                text = "Sign Up",
                color = Pink40,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
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
        color = Color(0xFFE57373),
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
            containerColor = Color(0xFFFFEBEE)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = message,
            color = Color(0xFFE57373),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun handleGoogleSignInResult(
    result: androidx.activity.result.ActivityResult,
    context: android.content.Context,
    state: SignInFormState
) {
    if (result.resultCode == Activity.RESULT_OK) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            state.onGoogleSignInResult(account)
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
            state.onGoogleSignInResult(null)
        }
    } else {
        state.onGoogleSignInResult(null)
    }
}

private fun launchGoogleSignIn(
    context: android.content.Context,
    launcher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>
) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent)
}