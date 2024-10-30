package com.fernandokh.koonol_management.viewModel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.AuthApiService
import com.fernandokh.koonol_management.data.models.AuthModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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

    val rememberMe: StateFlow<Boolean> = tokenManager.rememberMe
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    init {
        viewModelScope.launch {
            val savedEmail = tokenManager.email.first()
            val savedPassword = tokenManager.password.first()

            _email.value = savedEmail
            _password.value = savedPassword
        }
    }

    fun changeEmail(emailData: String) {
        _email.value = emailData
    }

    fun changePassword(passwordData: String) {
        _password.value = passwordData
    }

    fun handleRememberMe(checked: Boolean) {
        viewModelScope.launch {
            tokenManager.saveRememberMe(checked)
        }
    }

    private fun handleCredentials(email: String, password: String) {
        viewModelScope.launch {
            if (rememberMe.value) {
                tokenManager.saveCredentials(email, password)
            } else {
                tokenManager.clearCredentials()
            }
        }
    }

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun dismissDialog() {
        _isShowDialog.value = false
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(password: String): Boolean = password.length >= 3

    fun login() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (isValidEmail(email.value) && isValidPassword(password.value)) {
                    val authModel = AuthModel(email = email.value, password = password.value)
                    val response = apiService.login(authModel)
                    response.data?.let { tokenManager.saveAccessToken(it.token) }
                    Log.i("dev-debug", "token: ${response.data?.token}")
                    handleCredentials(email.value, password.value)
                    _navigationEvent.send(NavigationEvent.AuthSuccess)
                } else {
                    throw IllegalArgumentException("Email o contraseña inválidos")
                }
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Credenciales no validas")
            } finally {
                _isLoading.value = false
                dismissDialog()
            }
        }
    }

    fun loginStorage(emailS: String, passwordS: String){
        viewModelScope.launch {
            try {
                if (isValidEmail(emailS) && isValidPassword(passwordS)) {
                    val authModel = AuthModel(email = emailS, password = passwordS)
                    val response = apiService.login(authModel)
                    Log.i("dev-debug", "token: ${response.data?.token}")
                    handleCredentials(emailS, passwordS)
                    _navigationEvent.send(NavigationEvent.AuthSuccess)
                } else {
                    throw IllegalArgumentException("Email o contraseña inválidos")
                }
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Credenciales no validas")
            }
        }
    }

}

sealed class NavigationEvent {
    object AuthSuccess : NavigationEvent()
}