package com.example.authapp.data.model

data class ErrorResponse(
    val message: String,
    val code: String,
    val details: Map<String, Any>? = null
)
