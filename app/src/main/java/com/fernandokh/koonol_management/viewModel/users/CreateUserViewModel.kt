package com.fernandokh.koonol_management.viewModel.users

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.ApiResponseError
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.UserApiService
import com.fernandokh.koonol_management.data.models.UserCreateModel
import com.fernandokh.koonol_management.utils.SelectOption
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CreateUserViewModel : ViewModel() {
    val optionsRol = listOf(
        SelectOption("Selecciona un rol", ""),
        SelectOption("Administrador", "670318104d9824b4da0d9a9b"),
        SelectOption("Gestor", "6704214d834d7e5203cc834d")
    )

    val optionsGender = listOf(
        SelectOption("Selecciona un género", ""),
        SelectOption("Masculino", "male"),
        SelectOption("Fenemino", "female"),
        SelectOption("Otro", "other")
    )

    private val apiService = RetrofitInstance.create(UserApiService::class.java)
    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _isLoadingCreate = MutableStateFlow(false)
    val isLoadingCreate: StateFlow<Boolean> = _isLoadingCreate

    private val _isPhoto = MutableStateFlow<String?>(null)
    val isPhoto: StateFlow<String?> = _isPhoto

    private val _isName = MutableStateFlow("")
    val isName: StateFlow<String> = _isName

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _dayOfBirth = MutableStateFlow<String?>(null)
    val dayOfBirth: StateFlow<String?> = _dayOfBirth

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _rol = MutableStateFlow(optionsRol[0])
    val rol: StateFlow<SelectOption> = _rol

    private val _gender = MutableStateFlow(optionsGender[0])
    val gender: StateFlow<SelectOption> = _gender

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun onNameChange(value: String) {
        _isName.value = value
    }

    fun onLastNameChange(value: String) {
        _lastName.value = value
    }

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onDayOfBirthChange(value: String?) {
        _dayOfBirth.value = value
    }

    fun onPhoneChange(value: String) {
        _phone.value = value
    }

    fun onGenderChange(value: SelectOption) {
        _gender.value = value
    }

    fun onRolChange(value: SelectOption) {
        _rol.value = value
    }

    fun onPhotoChange(value: String?) {
        _isPhoto.value = value
    }


    fun dismissDialog() {
        _isShowDialog.value = false
    }

    fun showDialog() {
        _isShowDialog.value = true
    }

    fun createUser() {

        if (_dayOfBirth.value == null) {
            return
        }
        val user = UserCreateModel(
            name = _isName.value.trim(),
            email = _email.value.trim(),
            password = _password.value,
            lastName = _lastName.value.trim(),
            photo = _isPhoto.value,
            phoneNumber = _phone.value,
            rolId = _rol.value.value,
            gender = _gender.value.value,
            birthday = _dayOfBirth.value!!
        )

        viewModelScope.launch {
            try {
                _isLoadingCreate.value = true
                apiService.createUser(user)
                showToast("Usuario agregado con éxito")
                _navigationEvent.send(NavigationEvent.UserCreated)
            } catch (e: HttpException) {
                val errorResponse = e.response()
                val errorBody = errorResponse?.errorBody()?.string()

                val gson = Gson()
                val error = gson.fromJson(errorBody, ApiResponseError::class.java)

                Log.e("dev-debug", "Error Body: $errorBody")
                showToast(error.message)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrio un error al borrar")
            } finally {
                _isLoadingCreate.value = false
                dismissDialog()
            }
        }
    }
}

sealed class NavigationEvent {
    object UserCreated : NavigationEvent()
}