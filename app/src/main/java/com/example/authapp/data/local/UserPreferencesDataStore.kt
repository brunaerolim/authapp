package com.example.authapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHOTO_URL = stringPreferencesKey("user_photo_url")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
        private val LAST_EMAIL = stringPreferencesKey("last_email")
    }

    val userPreferences: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            userId = preferences[USER_ID] ?: "",
            userName = preferences[USER_NAME] ?: "",
            userEmail = preferences[USER_EMAIL] ?: "",
            userPhotoUrl = preferences[USER_PHOTO_URL],
            isLoggedIn = preferences[IS_LOGGED_IN] ?: false,
            rememberMe = preferences[REMEMBER_ME] ?: false,
            lastEmail = preferences[LAST_EMAIL] ?: ""
        )
    }

    suspend fun saveUserData(
        userId: String,
        userName: String,
        userEmail: String,
        userPhotoUrl: String?
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_NAME] = userName
            preferences[USER_EMAIL] = userEmail
            preferences[USER_PHOTO_URL] = userPhotoUrl ?: ""
            preferences[IS_LOGGED_IN] = true
        }
    }

    suspend fun setRememberMe(remember: Boolean, email: String = "") {
        dataStore.edit { preferences ->
            preferences[REMEMBER_ME] = remember
            if (remember) {
                preferences[LAST_EMAIL] = email
            } else {
                preferences.remove(LAST_EMAIL)
            }
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            val rememberMe = preferences[REMEMBER_ME] ?: false
            val lastEmail = preferences[LAST_EMAIL] ?: ""

            preferences.clear()

            if (rememberMe) {
                preferences[REMEMBER_ME] = true
                preferences[LAST_EMAIL] = lastEmail
            }

            preferences[IS_LOGGED_IN] = false
        }
    }

    suspend fun updateLoginStatus(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }
}