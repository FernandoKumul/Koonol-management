package com.fernandokh.koonol_management.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.AuthApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "AUTH_PREFERENCES")

class TokenManager(private val context: Context) {

    private val authService = RetrofitInstance.create(AuthApiService::class.java)

    companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val EMAIL_KEY = stringPreferencesKey("email")
        val PASSWORD_KEY = stringPreferencesKey("password")
        val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
    }

    val accessToken: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[ACCESS_TOKEN_KEY] ?: ""
        }

    val rememberMe: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[REMEMBER_ME_KEY] ?: false
        }

    val email: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[EMAIL_KEY] ?: ""
        }

    val password: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PASSWORD_KEY] ?: ""
        }

    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun saveCredentials(email: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
            preferences[PASSWORD_KEY] = password
        }
    }

    suspend fun clearAccessToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
        }
    }

    suspend fun clearCredentials() {
        context.dataStore.edit { preferences ->
            preferences.remove(EMAIL_KEY)
            preferences.remove(PASSWORD_KEY)
        }
    }

    suspend fun saveRememberMe(checked: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_ME_KEY] = checked
        }
    }

    suspend fun isTokenValid(): Boolean {
        return try {
            val response = authService.validateUser("Bearer ${accessToken.first()}")
            response.statusCode == 200
        } catch (e: Exception) {
            false
        }
    }

}
