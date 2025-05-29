package com.example.authapp.ui.screen.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    state: SignInScreenState,
) {
    SignInContent(state)
}

@Composable
fun SignInContent(state: SignInScreenState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        SignInForm(state.formState)

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = state.onNavigateToSignUp) {
            Text("Don't have an account? Sign Up")
        }

        // Loading indicator
        if (state.isLoading.value) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        // Error message
        val errorMessage = state.formState.errorMessage.value
        if (errorMessage.isNotEmpty()) {
            LaunchedEffect(errorMessage) {
                state.formState.onDismissError()
            }
        }
    }
}

@Composable
fun SignInForm(state: SignInFormState) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        OutlinedTextField(
            value = state.email.value,
            onValueChange = state.onEmailChanged,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        state.onEmailFocusLost()
                    }
                },
            singleLine = true,
            isError = state.emailError.value,
            shape = RoundedCornerShape(10.dp),
            supportingText = {
                if (state.emailError.value) {
                    Text(
                        text = if (state.email.value.trim().isEmpty()) {
                            "Email is required"
                        } else {
                            "Please insert a valid email"
                        },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password.value,
            onValueChange = state.onPasswordChanged,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        state.onPasswordFocusLost()
                    }
                },
            singleLine = true,
            isError = state.passwordError.value,
            shape = RoundedCornerShape(10.dp),
            supportingText = {
                if (state.passwordError.value) {
                    Text(
                        text = if (state.password.value.trim().isEmpty()) {
                            "Password is required"
                        } else {
                            "Password must be at least 6 characters"
                        },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = state.onSignIn,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isSignInEnabled.value
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = state.onGoogleSignIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In with Google")
        }
    }
}