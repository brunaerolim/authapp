package com.example.authapp.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.local.UserPreferencesDataStore
import com.example.authapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _showLoading = MutableStateFlow(false)
    val showLoading: StateFlow<Boolean> = _showLoading.asStateFlow()

    private val _showSignOutDialog = MutableStateFlow(false)
    val showSignOutDialog: StateFlow<Boolean> = _showSignOutDialog.asStateFlow()

    // Events
    private val _signOutSuccess = MutableSharedFlow<Unit>()
    val signOutSuccess: SharedFlow<Unit> = _signOutSuccess

    val currentUser = authRepository.currentUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val userPreferences = userPreferencesDataStore.userPreferences.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.example.authapp.data.local.UserPreferences(
            userId = "",
            userName = "",
            userEmail = "",
            userPhotoUrl = null,
            isLoggedIn = false,
            rememberMe = false,
            lastEmail = ""
        )
    )

    fun showSignOutDialog() {
        _showSignOutDialog.value = true
    }

    fun hideSignOutDialog() {
        _showSignOutDialog.value = false
    }

    fun onSignOut() {
        viewModelScope.launch {
            supervisorScope {
                _showLoading.value = true
                _showSignOutDialog.value = false

                try {
                    userPreferencesDataStore.clearUserData()

                    authRepository.signOut()

                    _signOutSuccess.emit(Unit)
                } catch (e: Exception) {
                    try {
                        userPreferencesDataStore.clearUserData()
                    } catch (clearException: Exception) {
                    }

                    _signOutSuccess.emit(Unit)
                } finally {
                    _showLoading.value = false
                }
            }
        }
    }

    fun refreshUserData() {
        viewModelScope.launch {
            _showLoading.value = true

            try {
                val currentFirebaseUser = authRepository.getCurrentUser()
                if (currentFirebaseUser != null) {
                    userPreferencesDataStore.saveUserData(
                        userId = currentFirebaseUser.uid,
                        userName = currentFirebaseUser.displayName ?: "",
                        userEmail = currentFirebaseUser.email ?: "",
                        userPhotoUrl = currentFirebaseUser.photoUrl?.toString()
                    )
                }
            } catch (e: Exception) {
                // Log error but don't crash
            } finally {
                _showLoading.value = false
            }
        }
    }
}