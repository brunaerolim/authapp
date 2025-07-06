package com.example.authapp.data.repository.auth

import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import com.example.authapp.core.utils.Resource
import com.example.authapp.data.remote.FireBaseAuthDataSource
import com.example.authapp.domain.model.User
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val dataSource: FireBaseAuthDataSource
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = AuthStateListener { auth ->
            trySend(auth.currentUser?.toUser())
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser?.toUser())

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    override suspend fun checkIfUserExists(email: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, "temp_password_check_123").await()
            auth.currentUser?.delete()?.await()
            false
        } catch (e: FirebaseAuthUserCollisionException) {
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun checkUserExists(email: String): Resource<Boolean> {
        return try {
            val exists = checkIfUserExists(email)
            Resource.Success(exists)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Email/Password Authentication
    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.toUser()

            if (user != null) {
                Resource.Success(user)
            } else {
                Resource.Failure(Exception("Authentication failed"))
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Resource.Failure(Exception("No account found with this email. Please create an account first."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Failure(Exception("Invalid email or password. Please try again."))
        } catch (e: Exception) {
            Resource.Failure(Exception("Failed to login: ${e.message}"))
        }
    }

    override suspend fun signIn(email: String, password: String): Resource<AuthResult> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result)
        } catch (e: FirebaseAuthInvalidUserException) {
            Resource.Failure(Exception("There's no user record corresponding to this identifier. Please create an account."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Failure(Exception("Invalid email or password. Please try again."))
        } catch (e: Exception) {
            Resource.Failure(Exception("Failed to login: ${e.message}"))
        }
    }

    override suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Resource<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            )?.await()

            val user = result.user?.toUser()
            if (user != null) {
                Resource.Success(user)
            } else {
                Resource.Failure(Exception("Failed to create account"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Failure(Exception("There is already a user with this email. Please sign in."))
        } catch (e: FirebaseAuthWeakPasswordException) {
            Resource.Failure(Exception("Password is too weak. Please choose a stronger one."))
        } catch (e: Exception) {
            Resource.Failure(Exception("Failed to create account: ${e.message}"))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String
    ): Resource<AuthResult> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            )?.await()

            Resource.Success(result)
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Failure(Exception("There is already a user with this email. Please choose another one."))
        } catch (e: FirebaseAuthWeakPasswordException) {
            Resource.Failure(Exception("Password is too weak. Please choose a stronger one."))
        } catch (e: Exception) {
            Resource.Failure(Exception("Failed to create account: ${e.message}"))
        }
    }

    // Google Sign-In with Credential Manager API
    override suspend fun signInWithGoogle(credentialResponse: GetCredentialResponse): Resource<User> {
        return try {
            val credential = credentialResponse.credential

            when (credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken
                            signInWithGoogleIdToken(idToken)
                        } catch (e: GoogleIdTokenParsingException) {
                            Resource.Failure(Exception("Invalid Google ID token response: ${e.message}"))
                        }
                    } else {
                        Resource.Failure(Exception("Unexpected credential type"))
                    }
                }

                else -> {
                    Resource.Failure(Exception("Unexpected credential type"))
                }
            }
        } catch (e: Exception) {
            Resource.Failure(Exception("Failed to sign in with Google: ${e.message}"))
        }
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): Resource<User> {
        return try {
            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(authCredential).await()
            val user = result.user?.toUser()

            if (user != null) {
                Resource.Success(user)
            } else {
                Resource.Failure(Exception("Failed to authenticate with Google"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Failure(Exception("An account already exists with the same email address but different sign-in credentials."))
        } catch (e: Exception) {
            Resource.Failure(Exception("Failed to authenticate with Google: ${e.message}"))
        }
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            // Log the error but do not fail sign-out
            android.util.Log.w("AuthRepository", "Error during sign-out", e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return dataSource.sendPasswordResetEmail(email)
    }

    override suspend fun confirmPasswordReset(code: String, newPassword: String): Result<Unit> {
        return dataSource.confirmPasswordReset(code, newPassword)
    }

    private fun FirebaseUser?.toUser(): User? {
        return this?.let {
            User(
                id = it.uid,
                name = it.displayName,
                email = it.email,
                photoUrl = it.photoUrl?.toString()
            )
        }
    }
}