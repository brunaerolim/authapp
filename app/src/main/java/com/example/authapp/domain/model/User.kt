package com.example.authapp.domain.model

data class User(
    val id: String,
    val name: String?,
    val email: String?,
    val photoUrl: String? = null
) {
    val displayName: String
        get() = name?.takeIf { it.isNotBlank() } ?: email?.substringBefore("@") ?: "User"

    val profileImageUrl: String
        get() = photoUrl
            ?: "https://ui-avatars.com/api/?name=${displayName}&background=6366f1&color=fff"
}