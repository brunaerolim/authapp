package com.example.authapp.data.repository.auth

import androidx.credentials.GetCredentialResponse
import com.example.authapp.core.utils.Resource
import com.example.authapp.domain.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>

    suspend fun checkIfUserExists(email: String): Boolean
    suspend fun checkUserExists(email: String): Resource<Boolean>
    fun getCurrentUser(): FirebaseUser?

    // Email/Password Auth
    suspend fun signInWithEmailAndPassword(email: String, password: String): Resource<User>
    suspend fun signIn(email: String, password: String): Resource<AuthResult>
    suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Resource<User>
    suspend fun signUp(email: String, password: String, name: String): Resource<AuthResult>

    // Google Sign-In with Credential Manager API
    suspend fun signInWithGoogle(credentialResponse: GetCredentialResponse): Resource<User>
    suspend fun signInWithGoogleIdToken(idToken: String): Resource<User>

    // Sign-out
    suspend fun signOut()

    // Password Reset
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun confirmPasswordReset(code: String, newPassword: String): Result<Unit>
}