package com.example.authapp.presentation.screen.signin

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.R
import com.example.authapp.presentation.theme.PastelPinkLight
import com.example.authapp.presentation.theme.Pink40
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    state: SignInFormState
) {
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                state.onGoogleSignInResult(account)
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG)
                    .show()
                state.onGoogleSignInResult(null)
            }
        } else {
            state.onGoogleSignInResult(null)
        }
    }

    LaunchedEffect(Unit) {
        state.startGoogleSignIn.collect {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher),
                contentDescription = null,
                tint = PastelPinkLight,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(40.dp)
            )
            Text(
                text = "Welcome Back!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Pink40,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to your account",
                fontSize = 16.sp,
                color = Pink40.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email Field
            OutlinedTextField(
                value = state.email.value,
                onValueChange = state.onEmailChanged,
                label = {
                    Text(
                        "Email",
                        color = Pink40.copy(alpha = 0.7f)
                    )
                },
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

            if (state.emailError.value) {
                Text(
                    text = if (state.email.value.trim().isEmpty()) {
                        "Email is required"
                    } else {
                        "Please insert a valid email"
                    },
                    color = Color(0xFFE57373),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = state.password.value,
                onValueChange = state.onPasswordChanged,
                label = {
                    Text(
                        "Password",
                        color = Pink40.copy(alpha = 0.7f)
                    )
                },
                visualTransformation = if (state.isPasswordVisible.value) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
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
                    IconButton(onClick = state.onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (state.isPasswordVisible.value) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            contentDescription = if (state.isPasswordVisible.value) {
                                "Hide password"
                            } else {
                                "Show password"
                            },
                            tint = Pink40
                        )
                    }
                }
            )

            if (state.passwordError.value) {
                Text(
                    text = if (state.password.value.trim().isEmpty()) {
                        "Password is required"
                    } else {
                        "Password must be at least 6 characters"
                    },
                    color = Color(0xFFE57373),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Remember Me Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.rememberMe.value,
                    onCheckedChange = state.onRememberMeChanged,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Pink40,
                        uncheckedColor = Pink40.copy(alpha = 0.6f),
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Remember me",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Pink40.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign In Button
            Button(
                onClick = state.onSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = state.isSignInEnabled.value && !state.isLoading.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pink40,
                    contentColor = Color.White,
                    disabledContainerColor = Pink40.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (state.isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Sign In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Pink40.copy(alpha = 0.3f)
                )
                Text(
                    text = "  OR  ",
                    color = Pink40.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Pink40.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google Sign In Button
            OutlinedButton(
                onClick = state.onGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isLoading.value,
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
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Pink40,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Sign In with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = Pink40.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = state.onNavigateToSignUp,
                    enabled = !state.isLoading.value
                ) {
                    Text(
                        text = "Sign Up",
                        color = Pink40,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error Message
            if (state.errorMessage.value.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = state.errorMessage.value,
                        color = Color(0xFFE57373),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}