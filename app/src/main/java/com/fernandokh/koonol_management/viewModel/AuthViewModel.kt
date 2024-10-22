package com.fernandokh.koonol_management.viewModel

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.ApiResponseError
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.AuthApiService
import com.fernandokh.koonol_management.data.models.AuthModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "TOKEN_PREFERENCES")

class AuthViewModel(private val context: Context) : ViewModel() {

    private val apiService = RetrofitInstance.create(AuthApiService::class.java)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun changeEmail(emailData: String) {
        _email.value = emailData
    }

    fun changePassword(passwordData: String) {
        _password.value = passwordData
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(password: String): Boolean = password.length >= 6

    private suspend fun keepToken(context: Context, token: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("token")] = token
        }
    }

    fun getToken(context: Context): Flow<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey("access_token")] ?: ""
            }
    }

    fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                if (isValidEmail(email.value) && isValidPassword(password.value)) {
                    val authModel = AuthModel(email = email.value, password = password.value)
                    val response = apiService.login(authModel)
                    if (response.data != null) {
                        Log.i("dev-devug", "token: ${response.data.token}")
                        keepToken(context, response.data.token)
                        _navigationEvent.send(NavigationEvent.AuthSuccess)
                    } else {
                        throw IllegalArgumentException("Error al iniciar sesión")
                    }
                } else {
                    throw IllegalArgumentException("Email o contraseña inválidos")
                }
            } catch (e: HttpException) {
                val errorResponse = e.response()
                val errorBody = errorResponse?.errorBody()?.string()

                val gson = Gson()
                val error = gson.fromJson(errorBody, ApiResponseError::class.java)

                Log.e("dev-debug", "Error Body: $errorBody")
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ah ocurrido un error")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class NavigationEvent {
    object AuthSuccess : NavigationEvent()
}