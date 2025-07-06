package com.example.authapp.data.repository.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.authapp.BuildConfig
import com.example.authapp.core.utils.Resource
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInManager @Inject constructor(
    private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)
    private val webClientId = BuildConfig.WEB_CLIENT_ID


    /**
     * Generates an nonce secure
     */
    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    suspend fun signInWithAuthorizedAccounts(): Resource<AuthCredential> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .setNonce(generateNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            handleCredentialResponse(result)
        } catch (e: NoCredentialException) {
            Resource.Failure(Exception("No valid account found"))
        } catch (e: GetCredentialException) {
            Resource.Failure(Exception("Error on getting credential: ${e.message}"))
        } catch (e: CancellationException) {
            Resource.Failure(Exception("Operation canceled by user"))
        } catch (e: Exception) {
            Resource.Failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    suspend fun signInWithAnyGoogleAccount(): Resource<AuthCredential> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setNonce(generateNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            handleCredentialResponse(result)
        } catch (e: GetCredentialException) {
            Resource.Failure(Exception("Error on Sign in with Google: ${e.message}"))
        } catch (e: CancellationException) {
            Resource.Failure(Exception("Operation canceled by user"))
        } catch (e: Exception) {
            Resource.Failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    /**
     * Sign-in using Google's ID token
     */
    suspend fun signInWithGoogleButton(): Resource<AuthCredential> {
        return try {
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .setNonce(generateNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            handleCredentialResponse(result)
        } catch (e: GetCredentialException) {
            Resource.Failure(Exception("Error on Sign in with Google: ${e.message}"))
        } catch (e: CancellationException) {
            Resource.Failure(Exception("Operation canceled by user"))
        } catch (e: Exception) {
            Resource.Failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    private fun handleCredentialResponse(result: GetCredentialResponse): Resource<AuthCredential> {
        return try {
            val credential = result.credential

            when (credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken
                        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                        Resource.Success(authCredential)
                    } else {
                        Resource.Failure(Exception("Unknown credential type"))
                    }
                }

                else -> {
                    Resource.Failure(Exception("Credential type unexpected"))
                }
            }
        } catch (e: GoogleIdTokenParsingException) {
            Resource.Failure(Exception("Google ID Token invalid: ${e.message}"))
        } catch (e: Exception) {
            Resource.Failure(Exception("Error on processing credential: ${e.message}"))
        }
    }

    /**
     * Clean credential state (for sign-out)
     */
    suspend fun clearCredentialState() {
        try {
            credentialManager.clearCredentialState(
                androidx.credentials.ClearCredentialStateRequest()
            )
        } catch (e: Exception) {
            android.util.Log.w("GoogleSignInManager", "Error on cleaning initial state", e)
        }
    }
}