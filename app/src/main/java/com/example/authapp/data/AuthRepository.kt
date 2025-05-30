package com.example.authapp.data

import com.example.authapp.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Resource<User>
    suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Resource<User>

    suspend fun signInWithGoogle(credential: AuthCredential): Resource<User>
    suspend fun signOut()
    suspend fun checkIfUserExists(email: String): Boolean

    suspend fun signIn(email: String, password: String): Resource<AuthResult>
    suspend fun signUp(email: String, password: String, name: String): Resource<AuthResult>
    suspend fun checkUserExists(email: String): Resource<Boolean>
    fun getCurrentUser(): FirebaseUser?
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
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
            val methods = auth.fetchSignInMethodsForEmail(email).await()
            methods.signInMethods?.isNotEmpty() == true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun checkUserExists(email: String): Resource<Boolean> {
        return try {
            val methods = auth.fetchSignInMethodsForEmail(email).await()
            Resource.Success(methods.signInMethods?.isNotEmpty() == true)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<User> {
        return try {
            if (!checkIfUserExists(email)) {
                return Resource.Failure(Exception("No account found with this email. Please create an account first."))
            }

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
            Resource.Failure(Exception("Login failed: ${e.message}"))
        }
    }

    override suspend fun signIn(email: String, password: String): Resource<AuthResult> {
        return try {
            if (!checkIfUserExists(email)) {
                return Resource.Failure(Exception("No account found with this email. Please create an account first."))
            }

            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result)
        } catch (e: FirebaseAuthInvalidUserException) {
            Resource.Failure(Exception("No account found with this email. Please create an account first."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Failure(Exception("Invalid email or password. Please try again."))
        } catch (e: Exception) {
            Resource.Failure(Exception("Login failed: ${e.message}"))
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
                Resource.Failure(Exception("Account creation failed"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Failure(Exception("An account with this email already exists. Please sign in instead."))
        } catch (e: FirebaseAuthWeakPasswordException) {
            Resource.Failure(Exception("Password is too weak. Please choose a stronger password."))
        } catch (e: Exception) {
            Resource.Failure(Exception("Account creation failed: ${e.message}"))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String
    ): Resource<AuthResult> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            // Update the user's profile with the name
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            )?.await()

            Resource.Success(result)
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Failure(Exception("An account with this email already exists. Please sign in instead."))
        } catch (e: FirebaseAuthWeakPasswordException) {
            Resource.Failure(Exception("Password is too weak. Please choose a stronger password."))
        } catch (e: Exception) {
            Resource.Failure(Exception("Account creation failed: ${e.message}"))
        }
    }

    // FIXED: Implementation of signInWithGoogle that returns Resource<User>
    override suspend fun signInWithGoogle(credential: AuthCredential): Resource<User> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            val user = result.user?.toUser()

            if (user != null) {
                Resource.Success(user)
            } else {
                Resource.Failure(Exception("Google sign in failed"))
            }
        } catch (e: Exception) {
            Resource.Failure(Exception("Google sign in failed: ${e.message}"))
        }
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
            googleSignInClient.signOut().await()
        } catch (e: Exception) {
            // Log error but don't throw
        }
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