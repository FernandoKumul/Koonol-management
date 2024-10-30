package com.fernandokh.koonol_management.viewModel.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.UserApiService
import com.fernandokh.koonol_management.data.models.ChangePasswordModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChangePasswordViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            return ChangePasswordViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ChangePasswordViewModel(private val tokenManager: TokenManager) : ViewModel() {
    data class FormErrors(
        val password: String? = null,
        val confirmPassword: String? = null,
    ) {
        fun allErrors(): List<String?> {
            return listOf(
                password,
                confirmPassword
            )
        }
    }

    data class PasswordForm(
        val password: String = "",
        val confirmPassword: String = "",
    )

    private val apiService = RetrofitInstance.create(UserApiService::class.java)

    private val _accessToken = MutableStateFlow("")
    val accessToken: StateFlow<String> = _accessToken

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _isLoadingUpdate = MutableStateFlow(false)
    val isLoadingUpdate: StateFlow<Boolean> = _isLoadingUpdate

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _formErrors = MutableStateFlow(FormErrors())
    val formErrors: StateFlow<FormErrors> = _formErrors

    private val _form = MutableStateFlow(PasswordForm())
    val form: StateFlow<PasswordForm> = _form

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun dismissDialog() {
        _isShowDialog.value = false
    }

    fun showDialog() {
        _isShowDialog.value = true
    }

    init {
        viewModelScope.launch {
            val savedToken = tokenManager.accessToken.first()
            _accessToken.value = savedToken
        }
    }

    fun changePassword(accessToken: String) {
        val password = ChangePasswordModel(password = _form.value.password)

        viewModelScope.launch {
            try {
                _isLoadingUpdate.value = true
                apiService.changePasswordProfile(
                    "Bearer $accessToken",
                    password
                )
                showToast("Contraseña actualizada con éxito")
                _navigationEvent.send(NavigationEvent.Navigate)
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error api cambiar la contraseña: $errorMessage")
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrio un error cambiar la contraseña")
            } finally {
                _isLoadingUpdate.value = false
                dismissDialog()
            }
        }
    }


    fun onPasswordChange(value: String) {
        _form.value = _form.value.copy(password = value)
        validatePassword()
        validateConfirmPassword()
    }

    fun onConfirmPasswordChange(value: String) {
        _form.value = _form.value.copy(confirmPassword = value)
        validateConfirmPassword()
    }

    private fun validatePassword() {
        val password = _form.value.password
        if (password.length < 3) {
            _formErrors.value = _formErrors.value.copy(password = "La contraseña debe de tener al menos 3 caracteres")
        } else {
            _formErrors.value = _formErrors.value.copy(password = null)
        }
    }

    private fun validateConfirmPassword() {
        val confirmPassword = _form.value.confirmPassword
        val password = _form.value.password
        if (password != confirmPassword && password.isNotEmpty() && _formErrors.value.password == null) {
            _formErrors.value = _formErrors.value.copy(confirmPassword = "Las contraseñas no coinciden")
        } else {
            _formErrors.value = _formErrors.value.copy(confirmPassword = null)
        }
    }

    fun isFormValid(): Boolean {
        validatePassword()
        validateConfirmPassword()
        return _formErrors.value.allErrors().all { it === null }
    }


}