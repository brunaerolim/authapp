package com.example.authapp.data.remote

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FireBaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = suspendCoroutine { cont ->
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(Result.success(Unit))
                } else {
                    cont.resume(Result.failure(task.exception ?: Exception("Unknown error")))
                }
            }
    }

    suspend fun confirmPasswordReset(code: String, newPassword: String): Result<Unit> =
        suspendCoroutine { cont ->
            firebaseAuth.confirmPasswordReset(code, newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(Result.success(Unit))
                    } else {
                        cont.resume(Result.failure(task.exception ?: Exception("Unknown error")))
                    }
                }
        }
}