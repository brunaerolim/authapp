package com.example.authapp.ui.screen.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.authapp.R
import com.example.authapp.ui.theme.PastelBackground
import com.example.authapp.ui.theme.PastelPink
import com.example.authapp.ui.theme.PastelPinkDark
import com.example.authapp.ui.theme.PastelPinkLight
import com.example.authapp.ui.theme.PastelPinkTheme
import com.example.authapp.ui.theme.PastelRose
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    state: SignInScreenState,
) {
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                state.onGoogleSignInResult(credential)
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    PastelPinkTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SignInContent(state, googleSignInLauncher, context)
        }
    }
}

@Composable
fun SignInContent(
    state: SignInScreenState,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
    context: Context
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PastelBackground,
                        PastelRose.copy(alpha = 0.3f),
                        PastelPinkLight.copy(alpha = 0.5f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícone decorativo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = PastelPink.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌸",
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = PastelPinkDark,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Sign in to continue your journey",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PastelPinkDark.copy(alpha = 0.7f)
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    SignInForm(state.formState)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = PastelPink.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "  or  ",
                            style = MaterialTheme.typography.bodySmall,
                            color = PastelPinkDark.copy(alpha = 0.7f)
                        )
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = PastelPink.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(context.getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build()
                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PastelPinkDark
                        ),
                        border = BorderStroke(1.dp, PastelPink),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🔍") // Emoji temporário
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continue with Google")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = state.onNavigateToSignUp,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = PastelPinkDark
                )
            ) {
                Text("Don't have an account? Sign Up")
            }

            if (state.isLoading.value) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    color = PastelPink
                )
            }

            val errorMessage = state.formState.errorMessage.value
            if (errorMessage.isNotEmpty()) {
                LaunchedEffect(errorMessage) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    state.formState.onDismissError()
                }
            }
        }
    }
}

@Composable
fun SignInForm(
    state: SignInFormState
) {
    Column {
        // Email Field
        OutlinedTextField(
            value = state.email.value,
            onValueChange = state.onEmailChanged,
            label = {
                Text(
                    "Email",
                    color = PastelPinkDark.copy(alpha = 0.7f)
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
                focusedBorderColor = PastelPink,
                unfocusedBorderColor = PastelPink.copy(alpha = 0.5f),
                focusedLabelColor = PastelPinkDark,
                cursorColor = PastelPink,
                errorBorderColor = Color(0xFFE57373),
                errorLabelColor = Color(0xFFE57373)
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = PastelPink
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
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
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
                    color = PastelPinkDark.copy(alpha = 0.7f)
                )
            },
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
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PastelPink,
                unfocusedBorderColor = PastelPink.copy(alpha = 0.5f),
                focusedLabelColor = PastelPinkDark,
                cursorColor = PastelPink,
                errorBorderColor = Color(0xFFE57373),
                errorLabelColor = Color(0xFFE57373)
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = PastelPink
                )
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
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign In Button
        Button(
            onClick = state.onSignIn,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isSignInEnabled.value,
            colors = ButtonDefaults.buttonColors(
                containerColor = PastelPink,
                contentColor = Color.White,
                disabledContainerColor = PastelPink.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                "Sign In",
                modifier = Modifier.padding(vertical = 4.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}