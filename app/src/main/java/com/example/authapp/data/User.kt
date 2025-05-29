package com.example.authapp.data

data class User(
    val id: String,
    val name: String?,
    val email: String?,
    val photoUrl: String? = null
)
