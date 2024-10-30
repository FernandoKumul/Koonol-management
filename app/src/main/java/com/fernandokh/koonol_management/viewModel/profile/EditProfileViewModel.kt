package com.fernandokh.koonol_management.viewModel.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.UserApiService
import com.fernandokh.koonol_management.data.models.ProfileEditModel
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.utils.evaluateHttpException
import com.fernandokh.koonol_management.utils.formatIsoDateToDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EditProfileViewModelFactory(private val tokenManager: TokenManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class EditProfileViewModel(private val tokenManager: TokenManager) : ViewModel() {

    data class FormErrors(
        val nameError: String? = null,
        val lastNameError: String? = null,
        val birthdayError: String? = null,
        val genderError: String? = null,
        val phoneError: String? = null,
    ) {
        fun allErrors(): List<String?> {
            return listOf(
                nameError,
                lastNameError,
                birthdayError,
                genderError,
                phoneError
            )
        }
    }

    data class FormUser(
        val name: String = "",
        val lastName: String = "",
        val birthday: String? = null,
        val gender: SelectOption,
        val phone: String = ""
    )

    val optionsGender = listOf(
        SelectOption("Selecciona un género", ""),
        SelectOption("Masculino", "male"),
        SelectOption("Fenemino", "female"),
        SelectOption("Otro", "other")
    )

    private val apiService = RetrofitInstance.create(UserApiService::class.java)

    private val _accessToken = MutableStateFlow("")
    val accessToken: StateFlow<String> = _accessToken

    private val _isUser = MutableStateFlow<UserInModel?>(null)
    val isUser: StateFlow<UserInModel?> = _isUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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

    private val _formUser = MutableStateFlow(FormUser(gender = optionsGender[0]))
    val formUser: StateFlow<FormUser> = _formUser

    private val _isPhoto = MutableStateFlow<String?>(null)
    val isPhoto: StateFlow<String?> = _isPhoto

    private val _dirtyForm = MutableStateFlow(false)

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
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

    init {
        viewModelScope.launch {
            val savedToken = tokenManager.accessToken.first()
            _accessToken.value = savedToken
        }
    }

    //Main functions

    fun getProfile(accessToken: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response =
                    apiService.getProfile("Bearer $accessToken")
                val data = response.data
                _isUser.value = data
                _isPhoto.value = response.data?.photo
                _formUser.value = FormUser(
                    name = data?.name ?: "",
                    lastName = data?.lastName ?: "",
                    birthday = response.data?.birthday?.let { formatIsoDateToDate(it) },
                    phone = data?.phoneNumber ?: "",
                    gender = optionsGender.find { it.value == data?.gender } ?: optionsGender[0]
                )
                Log.i("dev-debug", "Perfil obtenido con éxito")
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error al obtener el perfil: $errorMessage")
                _isUser.value = null
            } catch (e: Exception) {
                Log.e("dev-debug", e.message ?: "Ha ocurrido un error")
                _isUser.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(accessToken: String) {
        val user = ProfileEditModel(
            name = _formUser.value.name.trim(),
            lastName = _formUser.value.lastName.trim(),
            photo = _isPhoto.value,
            phoneNumber = _formUser.value.phone,
            gender = _formUser.value.gender.value,
            birthday = _formUser.value.birthday!!
        )
        Log.i("dev-debug", user.toString())

        viewModelScope.launch {
            try {
                _isLoadingUpdate.value = true
                apiService.updateProfile(
                    "Bearer $accessToken",
                    user
                )
                showToast("Perfil actualizado con éxito")
                _navigationEvent.send(NavigationEvent.Navigate)
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error api: $errorMessage")
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrio un error al actualizar el perfil")
            } finally {
                _isLoadingUpdate.value = false
                dismissDialog()
            }
        }
    }


    //Cambios del formulario y validaciones

    fun onNameChange(value: String) {
        _formUser.value = _formUser.value.copy(name = value)
        if (_dirtyForm.value) {
            validateName()
        }
    }

    fun onLastNameChange(value: String) {
        _formUser.value = _formUser.value.copy(lastName = value)
        if (_dirtyForm.value) {
            validateLastName()
        }
    }

    fun onDayOfBirthChange(value: String?) {
        _formUser.value = _formUser.value.copy(birthday = value)
        if (_dirtyForm.value) {
            validateDayOfBirth()
        }
    }

    fun onPhoneChange(value: String) {
        _formUser.value = _formUser.value.copy(phone = value)
        if (_dirtyForm.value) {
            validatePhoneNumber()
        }
    }

    fun onGenderChange(value: SelectOption) {
        _formUser.value = _formUser.value.copy(gender = value)
        if (_dirtyForm.value) {
            validateGender()
        }
    }

    private fun validateName() {
        val name = _formUser.value.name
        if (name.isBlank()) {
            _formErrors.value = _formErrors.value.copy(nameError = "El nombre es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(nameError = null)
        }
    }

    private fun validateLastName() {
        val name = _formUser.value.lastName
        if (name.isBlank()) {
            _formErrors.value = _formErrors.value.copy(lastNameError = "El apellido es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(lastNameError = null)
        }
    }

    private fun validateGender() {
        val gender = _formUser.value.gender
        if (gender.value == "") {
            _formErrors.value = _formErrors.value.copy(genderError = "El género es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(genderError = null)
        }
    }

    private fun validateDayOfBirth() {
        val dayOfBirth = _formUser.value.birthday
        if (dayOfBirth == null) {
            _formErrors.value =
                _formErrors.value.copy(birthdayError = "La fecha de nacimiento es requerida")
        } else {
            _formErrors.value = _formErrors.value.copy(birthdayError = null)
        }
    }

    private fun validatePhoneNumber() {
        val phoneNumber = _formUser.value.phone
        if (phoneNumber.length < 10) {
            _formErrors.value =
                _formErrors.value.copy(phoneError = "El número debe de tener al menos 10 números")
        } else {
            _formErrors.value = _formErrors.value.copy(phoneError = null)
        }
    }

    fun isFormValid(): Boolean {
        _dirtyForm.value = true
        validateName()
        validateLastName()
        validateGender()
        validateDayOfBirth()
        validatePhoneNumber()

        return _formErrors.value.allErrors().all { it === null }
    }
}