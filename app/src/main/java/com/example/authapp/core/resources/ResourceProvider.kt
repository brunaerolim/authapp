package com.example.authapp.core.resources

interface ResourceProvider {
    fun getString(resId: Int): String
}