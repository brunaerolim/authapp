package com.example.authapp.data.repository

import com.example.authapp.core.utils.Resource
import com.example.authapp.domain.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

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