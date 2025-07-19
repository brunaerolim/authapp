package com.example.authapp.core.di

import coil.intercept.Interceptor
import coil.intercept.Interceptor.*
import coil.request.ImageResult
import com.example.authapp.BuildConfig
import com.example.authapp.data.local.UserPreferencesDataStore
import kotlinx.coroutines.runBlocking


class AuthInterceptor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : Interceptor {

    override suspend fun intercept(chain: Chain): ImageResult {
        val originalRequest = chain.request


        val token = runBlocking {
            try {
                userPreferencesDataStore.userPreferences
            } catch (e: Exception) {
                null
            }
        }


        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("User-Agent", "AuthApp/${BuildConfig.VERSION_NAME}")

        token?.let { authToken ->
            requestBuilder.addHeader("Authorization", "Bearer $authToken")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

fun getBaseUrl(): String {
    return if (BuildConfig.DEBUG) {
        "https://your-dev-api.com/api/v1/"
    } else {
        "https://your-prod-api.com/api/v1/"
    }
}