package com.example.authapp.ui.screen.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    state: SignUpScreenState
) {

    SignUpContent(state = state)
}

@Composable
fun SignUpContent(state: SignUpScreenState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        SignUpForm(state.formState)

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = state.onNavigateToSignIn) {
            Text("Already have an account? Sign In")
        }

        if (state.isLoading.value) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        val errorMessage = state.formState.errorToastMessage.value
        if (errorMessage.isNotEmpty()) {
            LaunchedEffect(errorMessage) {
                state.formState.dismissSnackbar()
            }
        }
    }
}

@Composable
fun SignUpForm(state: SignUpFormState) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        // Name Field
        OutlinedTextField(
            value = state.name.value,
            onValueChange = state.onNameChanged,
            label = { Text("Full Name") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state.onNameFocusChanged(!it.isFocused) },
            singleLine = true,
            isError = state.nameError.value,
            shape = RoundedCornerShape(10.dp)
        )

        if (state.nameError.value) {
            Text(
                text = "Name must be at least 2 characters",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email.value,
            onValueChange = state.onEmailChanged,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state.onEmailFocusChanged(!it.isFocused) },
            singleLine = true,
            isError = state.emailError.value,
            shape = RoundedCornerShape(10.dp)
        )

        if (state.emailError.value) {
            Text(
                text = "Please enter a valid email",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password.value,
            onValueChange = state.onPasswordChanged,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state.onPasswordFocusChanged(!it.isFocused) },
            singleLine = true,
            isError = state.passwordError.value,
            shape = RoundedCornerShape(10.dp)
        )

        if (state.passwordError.value) {
            Text(
                text = "Password must be at least 6 characters",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.confirmPassword.value,
            onValueChange = state.onConfirmPasswordChanged,
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state.onConfirmPasswordFocusChanged(!it.isFocused) },
            singleLine = true,
            isError = state.confirmPasswordError.value,
            shape = RoundedCornerShape(10.dp)
        )

        if (state.confirmPasswordError.value) {
            Text(
                text = "Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = state.onSignUp,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.signUpEnabled.value
        ) {
            Text("Create Account")
        }
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(
        state = SignUpScreenState(
            isLoading = remember { mutableStateOf(false) },
            formState = SignUpFormState(
                name = remember { mutableStateOf("") },
                nameError = remember { mutableStateOf(false) },
                onNameChanged = {},
                onNameFocusChanged = {},
                email = remember { mutableStateOf("") },
                emailError = remember { mutableStateOf(false) },
                onEmailChanged = {},
                onEmailFocusChanged = {},
                password = remember { mutableStateOf("") },
                passwordError = remember { mutableStateOf(false) },
                onPasswordChanged = {},
                onPasswordFocusChanged = {},
                confirmPassword = remember { mutableStateOf("") },
                confirmPasswordError = remember { mutableStateOf(false) },
                onConfirmPasswordChanged = {},
                onConfirmPasswordFocusChanged = {},
                onSignUp = {},
                errorToastMessage = remember { mutableStateOf("") },
                dismissSnackbar = {},
                signUpEnabled = remember { mutableStateOf(false) }
            ),
            onNavigateToSignIn = {}
        )
    )
}