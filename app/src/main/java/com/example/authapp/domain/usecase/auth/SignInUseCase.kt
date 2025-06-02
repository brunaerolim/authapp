package com.example.authapp.domain.usecase.auth

import com.example.authapp.data.repository.AuthRepository
import com.example.authapp.data.local.UserPreferencesDataStore
import com.example.authapp.core.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    operator fun invoke(
        email: String,
        password: String,
        rememberMe: Boolean = false
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)

        try {
            when (val result = authRepository.signInWithEmailAndPassword(email, password)) {
                is Resource.Success -> {
                    val user = result.data
                    userPreferencesDataStore.saveUserData(
                        userId = user.id,
                        userName = user.name ?: "",
                        userEmail = user.email ?: "",
                        userPhotoUrl = user.photoUrl
                    )
                    userPreferencesDataStore.setRememberMe(rememberMe, email)
                    emit(Resource.Success(Unit))
                }
                is Resource.Failure -> {
                    emit(Resource.Failure(result.exception))
                }
                is Resource.Loading -> {
                    emit(Resource.Loading)
                }
            }
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }
}