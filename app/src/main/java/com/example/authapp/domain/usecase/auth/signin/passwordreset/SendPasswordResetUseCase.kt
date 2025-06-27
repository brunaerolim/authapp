package com.example.authapp.domain.usecase.auth.signin.passwordreset

import com.example.authapp.data.repository.auth.AuthRepository

class SendPasswordResetEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.sendPasswordResetEmail(email)
    }
}