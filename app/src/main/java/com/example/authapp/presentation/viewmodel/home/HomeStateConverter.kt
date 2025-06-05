package com.example.authapp.presentation.viewmodel.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.authapp.presentation.screen.home.HomeScreenState

@Composable
fun HomeViewModel.toScreenState(
    onNavigateToSignIn: () -> Unit,
    onSignOut: () -> Unit,
): HomeScreenState {
    return HomeScreenState(
        currentUser = currentUser.collectAsState(),
        userPreferences = userPreferences.collectAsState(),
        isLoading = showLoading.collectAsState(),
        showSignOutDialog = showSignOutDialog.collectAsState(),
        onSignOut = ::onSignOut,
        onShowSignOutDialog = ::showSignOutDialog,
        onHideSignOutDialog = ::hideSignOutDialog,
        onRefreshUserData = ::refreshUserData,
        onNavigateToSignIn = onNavigateToSignIn,
        onNavigateToSignOut = onSignOut
    )
}