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
import android.util.Base64
import org.json.JSONObject


class AuthViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AuthViewModel(val tokenManager: TokenManager) : ViewModel() {

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

    // Propiedades para almacenar los datos del usuario decodificados
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> get() = _userId

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> get() = _userName

    private val _rolId = MutableStateFlow<String?>(null)
    val rolId: StateFlow<String?> get() = _rolId

    private val _rolName = MutableStateFlow<String?>(null)
    val rolName: StateFlow<String?> get() = _rolName

    init {
        viewModelScope.launch {
            val savedEmail = tokenManager.email.first()
            val savedPassword = tokenManager.password.first()

            _email.value = savedEmail
            _password.value = savedPassword

            tokenManager.accessToken.first().let { token ->
                if (token.isNotEmpty()) {
                    Log.d("AuthViewModel", "Token encontrado: $token")
                    decodeJwt(token)?.let { decodedToken ->
                        _userId.value = decodedToken.optString("userId")
                        _userName.value = decodedToken.optString("userName")
                        _rolId.value = decodedToken.optString("rolId")
                        _rolName.value = decodedToken.optString("rolName")
                        Log.d("AuthViewModel", "UserId inicializado: ${_userId.value}")
                    }
                } else {
                    Log.e("AuthViewModel", "No se encontró un token al inicializar")
                }
            }
        }
    }
    fun setUserId(id: String?) {
        _userId.value = id
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

    public fun decodeJwt(token: String): JSONObject? {
        return try {
            val parts = token.split(".")
            if (parts.size < 3) {
                null
            } else {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
                JSONObject(payload)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun login() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (isValidEmail(email.value) && isValidPassword(password.value)) {
                    val authModel = AuthModel(email = email.value, password = password.value)
                    val response = apiService.login(authModel)
                    response.data?.let { token ->
                        // Guardar el token en el TokenManager
                        tokenManager.saveAccessToken(token.token)

                        // Decodificar el token para extraer los datos del usuario
                        decodeJwt(token.token)?.let { decodedToken ->
                            _userId.value = decodedToken.optString("userId")
                            _userName.value = decodedToken.optString("userName")
                            _rolId.value = decodedToken.optString("rolId")
                            _rolName.value = decodedToken.optString("rolName")
                            Log.d("AuthViewModel", "Decoded userId after login: ${_userId.value}")
                        }

                        Log.d("AuthViewModel", "Decoded userId: ${_userId.value}")
                    }

                    Log.d("AuthViewModel", "Decoded userId: ${_userId.value}")
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
                showToast(e.message ?: "Ha ocurrido un error")
            } finally {
                _isLoading.value = false
                dismissDialog()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                tokenManager.clearAccessToken()
                _userId.value = null
                _userName.value = null
                _rolId.value = null
                _rolName.value = null
                _navigationEvent.send(NavigationEvent.AuthSuccess)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast(e.message ?: "Ha ocurrido un error")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class NavigationEvent {
    object AuthSuccess : NavigationEvent()
}