package com.example.authapp.data.local

data class UserPreferences(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userPhotoUrl: String?,
    val isLoggedIn: Boolean,
    val rememberMe: Boolean,
    val lastEmail: String
)