package com.example.authapp.presentation.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.authapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.Flow

@Composable
fun rememberGoogleSignInLauncher(
    onResult: (GoogleSignInAccount?) -> Unit,
    context: Context
): ActivityResultLauncher<Intent> {
    return rememberLauncherForActivityResult(
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
}

@Composable
fun HandleGoogleSignIn(
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