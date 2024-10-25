package com.fernandokh.koonol_management.viewModel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.ApiResponseError
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.AuthApiService
import com.fernandokh.koonol_management.data.models.AuthModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AuthViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val apiService = RetrofitInstance.create(AuthApiService::class.java)

    val accessToken: StateFlow<String?> = tokenManager.accessToken
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

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

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }


    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(password: String): Boolean = password.length >= 6

    fun login() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (isValidEmail(email.value) && isValidPassword(password.value)) {
                    val authModel = AuthModel(email = email.value, password = password.value)
                    val response = apiService.login(authModel)
                    if (response.data != null) {
                        Log.i("dev-debug", "token: ${response.data.token}")
                        tokenManager.saveAccessToken(response.data.token)
                        _navigationEvent.send(NavigationEvent.AuthSuccess)
                    } else {
                        throw IllegalArgumentException("Error al iniciar sesión")
                    }
                } else {
                    throw IllegalArgumentException("Email o contraseña inválidos")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val apiResponseError = Gson().fromJson(errorBody, ApiResponseError::class.java)
                showToast(apiResponseError.message)
            } catch (e: Exception) {
                showToast("Error al iniciar sesión")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class NavigationEvent {
    object AuthSuccess : NavigationEvent()
}