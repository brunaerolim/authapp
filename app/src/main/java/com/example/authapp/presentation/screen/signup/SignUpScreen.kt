package com.example.authapp.presentation.screen.signup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.authapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalComposeUiApi::class)
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

    // Handle Google Sign-In flow
    HandleGoogleSignIn(
        startGoogleSignIn = state.startGoogleSignIn,
        context = context,
        launcher = googleSignInLauncher
    )

    // Hide keyboard when loading
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
        SignUpContent(
            state = state,
            paddingValues = paddingValues,
            keyboardController = keyboardController,
            focusManager = focusManager
        )
    }
}

@Composable
private fun rememberGoogleSignInLauncher(
    onResult: (GoogleSignInAccount?) -> Unit,
    context: Context
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            onResult(account)
        } catch (e: ApiException) {
            Toast.makeText(
                context,
                context.getString(R.string.google_signup_failed, e.message),
                Toast.LENGTH_LONG
            ).show()
            onResult(null)
        }
    } else {
        onResult(null)
    }
}

@Composable
private fun HandleGoogleSignIn(
    startGoogleSignIn: Flow<Unit>,
    context: Context,
    launcher: ActivityResultLauncher<Intent>
) {
    LaunchedEffect(Unit) {
        startGoogleSignIn.collect {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SignUpContent(
    state: SignUpFormState,
    paddingValues: PaddingValues,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
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
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SignUpHeader()
        }

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

@Composable
private fun SignUpHeader() {
    Column(
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SignUpForm(
    state: SignUpFormState,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Name Field
        NameTextField(
            value = state.name.value,
            onValueChange = state.onNameChanged,
            onFocusChanged = state.onNameFocusChanged,
            isError = state.nameError.value,
            focusManager = focusManager
        )

        // Email Field
        EmailTextField(
            value = state.email.value,
            onValueChange = state.onEmailChanged,
            onFocusChanged = state.onEmailFocusChanged,
            isError = state.emailError.value,
            focusManager = focusManager
        )

        // Password Field
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

        // Confirm Password Field
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
                    state.onSignUp()
                }
            }
        )
    }
}

@Composable
private fun NameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    isError: Boolean,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = stringResource(R.string.full_name_label),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        onFocusChanged(false)
                    }
                },
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            colors = createTextFieldColors()
        )

        if (isError) {
            ErrorText(
                text = stringResource(R.string.name_error_message),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    isError: Boolean,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = stringResource(R.string.email_label),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        onFocusChanged(false)
                    }
                },
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            colors = createTextFieldColors()
        )

        if (isError) {
            ErrorText(
                text = stringResource(R.string.email_invalid_error),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    isError: Boolean,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    focusManager: FocusManager,
    label: String,
    isLastField: Boolean = false,
    keyboardController: SoftwareKeyboardController? = null,
    onDone: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
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
                        imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = stringResource(
                            if (isVisible) R.string.hide_password else R.string.show_password
                        ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = if (isLastField) ImeAction.Done else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onDone?.invoke()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        onFocusChanged(false)
                    }
                },
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            colors = createTextFieldColors()
        )

        if (isError) {
            val errorMessage = if (label.contains("Confirm")) {
                stringResource(R.string.passwords_not_match_error)
            } else {
                stringResource(R.string.password_error_message)
            }

            ErrorText(
                text = errorMessage,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun TermsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SignUpButtons(
    state: SignUpFormState,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Create Account Button
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
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            if (state.isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.create_account_button),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Divider
        DividerWithText(text = stringResource(R.string.or_divider))

        // Google Sign Up Button
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

@Composable
private fun DividerWithText(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
        Text(
            text = "  $text  ",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )
        Divider(
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
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
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
                imageVector = Icons.Default.Search, // In a real app, use Google's icon
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.google_signup_button),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun SignInLink(
    onNavigateToSignIn: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
private fun ErrorMessage(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    if (errorMessage.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
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
        modifier = modifier.fillMaxWidth()
    )
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