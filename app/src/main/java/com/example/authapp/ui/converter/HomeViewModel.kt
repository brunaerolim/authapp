package com.example.authapp.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val showLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    // Events
    private val _signOutSuccess = MutableSharedFlow<Unit>()
    val signOutSuccess: SharedFlow<Unit> = _signOutSuccess

    val currentUser = authRepository.currentUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        null
    )

    fun onSignOut() {
        viewModelScope.launch {
            showLoading.value = true
            authRepository.signOut()
            _signOutSuccess.emit(Unit)
            showLoading.value = false
        }
    }
}