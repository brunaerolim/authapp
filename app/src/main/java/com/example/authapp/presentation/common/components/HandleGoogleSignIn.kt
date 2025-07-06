package com.example.authapp.presentation.common.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Composable
fun HandleGoogleCredentialManager(
    startGoogleSignIn: SharedFlow<Unit>,
    context: Context,
    onResult: (GetCredentialResponse) -> Unit,
    onError: (String) -> Unit,
    filterByAuthorizedAccounts: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        startGoogleSignIn.collect {
            coroutineScope.launch {
                try {
                    val credentialManager = CredentialManager.create(context)
                    val nonce = generateNonce()
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                        .setNonce(nonce)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val response = credentialManager.getCredential(
                        request = request,
                        context = context
                    )
                    onResult(response)
                } catch (e: NoCredentialException) {
                    onError("No Google account found on this device.")
                } catch (e: GetCredentialException) {
                    onError("Google sign in failed: ${e.errorMessage}")
                } catch (e: Exception) {
                    onError("Google sign in failed: ${e.message}")
                }
            }
        }
    }
}

private fun generateNonce(): String {
    val rawNonce = UUID.randomUUID().toString()
    val bytes = rawNonce.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
}