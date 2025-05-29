package com.example.authapp.ui.screen.home

import androidx.compose.runtime.State
import com.example.authapp.data.User

data class HomeScreenState(
    val user: State<User?>,
    val isLoading: State<Boolean>,
    val onSignOut: () -> Unit
)