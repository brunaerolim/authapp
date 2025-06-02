package com.example.authapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_PHOTO_URL_KEY = stringPreferencesKey("user_photo_url")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
        private val LAST_EMAIL_KEY = stringPreferencesKey("last_email")
    }

    val userPreferences: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            userId = preferences[USER_ID_KEY] ?: "",
            userName = preferences[USER_NAME_KEY] ?: "",
            userEmail = preferences[USER_EMAIL_KEY] ?: "",
            userPhotoUrl = preferences[USER_PHOTO_URL_KEY],
            isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false,
            rememberMe = preferences[REMEMBER_ME_KEY] ?: false,
            lastEmail = preferences[LAST_EMAIL_KEY] ?: ""
        )
    }

    suspend fun saveUserData(
        userId: String,
        userName: String,
        userEmail: String,
        userPhotoUrl: String? = null
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
            preferences[USER_EMAIL_KEY] = userEmail
            userPhotoUrl?.let { preferences[USER_PHOTO_URL_KEY] = it }
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    suspend fun setRememberMe(remember: Boolean, email: String = "") {
        dataStore.edit { preferences ->
            preferences[REMEMBER_ME_KEY] = remember
            if (remember && email.isNotEmpty()) {
                preferences[LAST_EMAIL_KEY] = email
            } else if (!remember) {
                preferences.remove(LAST_EMAIL_KEY)
            }
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_PHOTO_URL_KEY)
            preferences[IS_LOGGED_IN_KEY] = false
        }
    }
}
