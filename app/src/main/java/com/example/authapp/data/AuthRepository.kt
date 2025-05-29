package com.example.authapp.data

import com.example.authapp.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Resource<User>
    suspend fun signUpWithEmailAndPassword(name: String, email: String, password: String): Resource<User>
    suspend fun signInWithGoogle(credential: AuthCredential): Resource<User>
    suspend fun signOut()
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = AuthStateListener { auth ->
            trySend(auth.currentUser?.toUser())
        }
        auth.addAuthStateListener(listener)

        // Emit current user immediately
        trySend(auth.currentUser?.toUser())

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

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
                Resource.Failure(Exception("User data not available"))
            }
        } catch (e: Exception) {
            Resource.Failure(e)
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
                Resource.Failure(Exception("User registration failed"))
            }
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

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
            Resource.Failure(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().await()
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