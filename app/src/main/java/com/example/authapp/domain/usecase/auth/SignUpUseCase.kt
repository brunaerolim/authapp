package com.example.authapp.domain.usecase.auth

import com.example.authapp.data.repository.AuthRepository
import com.example.authapp.data.local.UserPreferencesDataStore
import com.example.authapp.core.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)

        try {
            when (val result = authRepository.signUpWithEmailAndPassword(name, email, password)) {
                is Resource.Success -> {
                    val user = result.data
                    userPreferencesDataStore.saveUserData(
                        userId = user.id,
                        userName = user.name ?: name,
                        userEmail = user.email ?: email,
                        userPhotoUrl = user.photoUrl
                    )
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