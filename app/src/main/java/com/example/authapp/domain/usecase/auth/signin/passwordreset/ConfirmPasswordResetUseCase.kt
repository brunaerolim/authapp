package com.example.authapp.domain.usecase.auth.signin.passwordreset

import com.example.authapp.data.repository.auth.AuthRepository

class ConfirmPasswordResetUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(code: String, newPassword: String): Result<Unit> {
        return repository.confirmPasswordReset(code, newPassword)
    }
}