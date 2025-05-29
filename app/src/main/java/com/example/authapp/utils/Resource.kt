package com.example.authapp.utils

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Failure(val exception: Throwable) : Resource<Nothing>()
}