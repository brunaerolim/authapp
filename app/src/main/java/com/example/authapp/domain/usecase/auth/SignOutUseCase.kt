package com.example.authapp.domain.usecase.auth

import com.example.authapp.data.repository.AuthRepository
import com.example.authapp.data.local.UserPreferencesDataStore
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    suspend operator fun invoke() {
        try {
            authRepository.signOut()
            userPreferencesDataStore.clearUserData()
        } catch (e: Exception) {
            // Log error but continue with local cleanup
            userPreferencesDataStore.clearUserData()
        }
    }
}