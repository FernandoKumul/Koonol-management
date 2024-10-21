package com.fernandokh.koonol_management.viewModel.users

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.ApiResponseError
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.UserApiService
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.data.models.UserUpdateModel
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.utils.formatIsoDateToDate
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EditUserViewModel : ViewModel() {
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
    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")

    private val apiService = RetrofitInstance.create(UserApiService::class.java)

    private val _isUser = MutableStateFlow<UserInModel?>(null)
    val isUser: StateFlow<UserInModel?> = _isUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _isLoadingUpdate = MutableStateFlow(false)
    val isLoadingUpdate: StateFlow<Boolean> = _isLoadingUpdate

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

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _formErrors = MutableStateFlow(FormErrors())
    val formErrors: StateFlow<FormErrors> = _formErrors

    private val _dirtyForm = MutableStateFlow(false)

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun onNameChange(value: String) {
        _isName.value = value
        if (_dirtyForm.value) {
            validateName()
        }
    }

    fun onLastNameChange(value: String) {
        _lastName.value = value
        if (_dirtyForm.value) {
            validateLastName()
        }
    }

    fun onEmailChange(value: String) {
        _email.value = value
        if (_dirtyForm.value) {
            validateEmail()
        }
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        if (_dirtyForm.value) {
            validatePassword()
        }
    }

    fun onDayOfBirthChange(value: String?) {
        _dayOfBirth.value = value
        if (_dirtyForm.value) {
            validateDayOfBirth()
        }
    }

    fun onPhoneChange(value: String) {
        _phone.value = value
        if (_dirtyForm.value) {
            validatePhoneNumber()
        }
    }

    fun onGenderChange(value: SelectOption) {
        _gender.value = value
        if (_dirtyForm.value) {
            validateGender()
        }
    }

    fun onRolChange(value: SelectOption) {
        _rol.value = value
        if (_dirtyForm.value) {
            validateRol()
        }
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

    fun getUser(userId: String?) {

        if (userId == null) {
            _isUser.value = null
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.getUserById(userId)
                _isUser.value = response.data
                _isName.value = response.data?.name ?: ""
                _lastName.value = response.data?.lastName ?: ""
                _email.value = response.data?.email ?: ""
                _isPhoto.value = response.data?.photo
                _dayOfBirth.value = response.data?.birthday?.let { formatIsoDateToDate(it) } //Checar esto
                _phone.value = response.data?.phoneNumber ?: ""
                _rol.value = optionsRol.find { it.text == response.data?.rol?.name } ?: optionsRol[0]
                _gender.value = optionsGender.find { it.value == response.data?.gender } ?: optionsGender[0]
                Log.i("dev-debug", "Usuario obtenido con éxito: $userId")
            } catch (e: HttpException) {
                val errorResponse = e.response()
                val errorBody = errorResponse?.errorBody()?.string()

                val gson = Gson()
                val error = gson.fromJson(errorBody, ApiResponseError::class.java)

                Log.e("dev-debug", "Error Body: $error")
                _isUser.value = null
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                _isUser.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser() {
        val user = UserUpdateModel(
            name = _isName.value.trim(),
            email = _email.value.trim(),
            password = if (password.value.isEmpty()) null else _password.value,
            lastName = _lastName.value.trim(),
            photo = _isPhoto.value,
            phoneNumber = _phone.value,
            rolId = _rol.value.value,
            gender = _gender.value.value,
            birthday = _dayOfBirth.value!!
        )
        Log.i("dev-debug", user.toString())

        viewModelScope.launch {
            try {
                _isLoadingUpdate.value = true
                apiService.updateUser(_isUser.value?.id ?: "", user)
                showToast("Usuario actualizado con éxito")
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
                _isLoadingUpdate.value = false
                dismissDialog()
            }
        }
    }

    private fun validateName() {
        val name = _isName.value
        if (name.isBlank()) {
            _formErrors.value = _formErrors.value.copy(nameError = "El nombre es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(nameError = null)
        }
    }

    private fun validateLastName() {
        val name = _lastName.value
        if (name.isBlank()) {
            _formErrors.value = _formErrors.value.copy(lastNameError = "El apellido es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(lastNameError = null)
        }
    }

    private fun validateEmail() {
        val email = _email.value
        if (email.isBlank()) {
            _formErrors.value = _formErrors.value.copy(emailError = "El correo es requerido")
        } else if (!emailPattern.matches(email)) {
            _formErrors.value =
                _formErrors.value.copy(emailError = "Ingresa el correo electrónico válido")
        } else {
            _formErrors.value = _formErrors.value.copy(emailError = null)
        }
    }

    private fun validateRol() {
        val rol = _rol.value
        if (rol.value == "") {
            _formErrors.value = _formErrors.value.copy(rolError = "El rol es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(rolError = null)
        }
    }

    private fun validateGender() {
        val gender = _gender.value
        if (gender.value == "") {
            _formErrors.value = _formErrors.value.copy(genderError = "El género es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(genderError = null)
        }
    }

    private fun validatePassword() {
        val password = _password.value
        if (password.length < 3 && password.isNotEmpty()) {
            _formErrors.value = _formErrors.value.copy(passwordError = "La contraseña debe de tener al menos 3 caracteres")
        } else {
            _formErrors.value = _formErrors.value.copy(passwordError = null)
        }
    }

    private fun validateDayOfBirth() {
        val dayOfBirth = _dayOfBirth.value
        if (dayOfBirth == null) {
            _formErrors.value =
                _formErrors.value.copy(birthdayError = "La fecha de nacimiento es requerida")
        } else {
            _formErrors.value = _formErrors.value.copy(birthdayError = null)
        }
    }

    private fun validatePhoneNumber() {
        val phoneNumber = _phone.value
        if (phoneNumber.length < 10) {
            _formErrors.value = _formErrors.value.copy(phoneError = "La contraseña es requerida")
        } else {
            _formErrors.value = _formErrors.value.copy(phoneError = null)
        }
    }


    fun isFormValid(): Boolean {
        _dirtyForm.value = true
        validateName()
        validateLastName()
        validateEmail()
        validateGender()
        validateRol()
        validatePassword()
        validateDayOfBirth()
        validatePhoneNumber()

        return _formErrors.value.allErrors().all { it === null }
    }
}