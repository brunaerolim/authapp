package com.example.authapp.presentation.screen.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.authapp.R
import com.example.authapp.presentation.common.HandleGoogleSignIn
import com.example.authapp.presentation.common.SignUpTopBar
import com.example.authapp.presentation.common.components.DividerWithText
import com.example.authapp.presentation.common.components.EmailTextField
import com.example.authapp.presentation.common.components.ErrorMessage
import com.example.authapp.presentation.common.components.GoogleSignUpButton
import com.example.authapp.presentation.common.components.NameTextField
import com.example.authapp.presentation.common.components.PasswordTextField
import com.example.authapp.presentation.common.components.TermsCheckbox
import com.example.authapp.presentation.common.rememberGoogleSignInLauncher

@Composable
fun SignUpScreen(
    state: SignUpFormState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val googleSignInLauncher = rememberGoogleSignInLauncher(
        onResult = state.onGoogleSignUpResult,
        context = context
    )

    HandleGoogleSignIn(
        startGoogleSignIn = state.startGoogleSignIn,
        context = context,
        launcher = googleSignInLauncher
    )

    LaunchedEffect(state.isLoading.value) {
        if (state.isLoading.value) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SignUpTopBar(onNavigateBack = state.onNavigateBack)
        }
    ) { paddingValues ->
        LazyColumn(
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
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)); SignUpHeader() }
            item {
                SignUpForm(
                    state = state,
                    keyboardController = keyboardController,
                    focusManager = focusManager
                )
            }
            item {
                TermsCheckbox(
                    checked = state.acceptTerms.value,
                    onCheckedChange = state.onAcceptTermsChanged
                )
            }
            item {
                SignUpButtons(
                    state = state,
                    keyboardController = keyboardController,
                    focusManager = focusManager
                )
            }
            item {
                SignInLink(
                    onNavigateToSignIn = state.onNavigateToSignIn,
                    enabled = !state.isLoading.value
                )
            }
            item {
                ErrorMessage(errorMessage = state.errorToastMessage.value)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SignUpHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_launcher),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.create_account_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.create_account_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SignInLink(
    onNavigateToSignIn: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
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
private fun SignUpForm(
    state: SignUpFormState,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NameTextField(
            value = state.name.value,
            onValueChange = state.onNameChanged,
            onFocusChanged = state.onNameFocusChanged,
            isError = state.nameError.value,
            focusManager = focusManager
        )
        EmailTextField(
            value = state.email.value,
            onValueChange = state.onEmailChanged,
            onFocusChanged = state.onEmailFocusChanged,
            isError = state.emailError.value,
            focusManager = focusManager
        )
        PasswordTextField(
            value = state.password.value,
            onValueChange = state.onPasswordChanged,
            onFocusChanged = state.onPasswordFocusChanged,
            isError = state.passwordError.value,
            isVisible = state.isPasswordVisible.value,
            onToggleVisibility = state.onTogglePasswordVisibility,
            focusManager = focusManager,
            label = stringResource(R.string.password_label)
        )
        PasswordTextField(
            value = state.confirmPassword.value,
            onValueChange = state.onConfirmPasswordChanged,
            onFocusChanged = state.onConfirmPasswordFocusChanged,
            isError = state.confirmPasswordError.value,
            isVisible = state.isConfirmPasswordVisible.value,
            onToggleVisibility = state.onToggleConfirmPasswordVisibility,
            focusManager = focusManager,
            label = stringResource(R.string.confirm_password_label),
            isLastField = true,
            keyboardController = keyboardController,
            onDone = {
                if (state.signUpEnabled.value) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    state.onSignUp()
                }
            }
        )
    }
}

@Composable
private fun SignUpButtons(
    state: SignUpFormState,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Button(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                state.onSignUp()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = state.signUpEnabled.value && !state.isLoading.value,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (state.isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = stringResource(R.string.create_account_button))
            }
        }

        DividerWithText(text = stringResource(R.string.or_divider))

        GoogleSignUpButton(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                state.onGoogleSignUp()
            },
            enabled = !state.isLoading.value
        )
    }
}