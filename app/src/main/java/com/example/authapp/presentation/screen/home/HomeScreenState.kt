package com.example.authapp.presentation.screen.home

import androidx.compose.runtime.State
import com.example.authapp.data.local.UserPreferences
import com.example.authapp.domain.model.User

data class HomeScreenState(
    val currentUser: State<User?>,
    val userPreferences: State<UserPreferences>,
    val isLoading: State<Boolean>,
    val showSignOutDialog: State<Boolean>,
    val onSignOut: () -> Unit,
    val onShowSignOutDialog: () -> Unit,
    val onHideSignOutDialog: () -> Unit,
    val onRefreshUserData: () -> Unit,
    val onNavigateToSignIn: () -> Unit,
    val onNavigateToSignOut: () -> Unit
)