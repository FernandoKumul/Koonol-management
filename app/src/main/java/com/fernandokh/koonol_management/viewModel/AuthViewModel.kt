package com.fernandokh.koonol_management.viewModel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.ApiResponseError
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.AuthApiService
import com.fernandokh.koonol_management.data.models.AuthModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel : ViewModel() {

    private val apiService = RetrofitInstance.create(AuthApiService::class.java)

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _accessToken = MutableStateFlow("")
    var accessToken: StateFlow<String> = _accessToken

    fun changeEmail(emailData: String) {
        _email.value = emailData
    }

    fun changePassword(passwordData: String) {
        _password.value = passwordData
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(password: String): Boolean = password.length >= 6

    fun login() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.i("dev-devug", "Entrando a login")
                if (isValidEmail(email.value) && isValidPassword(password.value)) {
                    Log.i("dev-devug", "Entrando a login if")
                    val authModel = AuthModel(email = email.value, password = password.value)
                    val response = apiService.login(authModel)
                    Log.i("dev-devug", "respuesta del login")
                    if (response.data != null) {
                        Log.i("dev-devug", "token")
                        _accessToken.value = response.data.token
                        Log.i("dev-devug", "token: ${_accessToken.value}")
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