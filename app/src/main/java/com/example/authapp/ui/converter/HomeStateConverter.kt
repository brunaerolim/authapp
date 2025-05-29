package com.example.authapp.ui.converter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.authapp.ui.screen.home.HomeScreenState

@Composable
fun HomeViewModel.toScreenState(
    onNavigateToSignIn: () -> Unit
) = HomeScreenState(
    user = currentUser.collectAsState(),
    isLoading = showLoading.collectAsState(),
    onSignOut = {
        onSignOut()
        onNavigateToSignIn()
    }
)