package com.fernandokh.koonol_management.viewModel.sellers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.SellerApiService
import com.fernandokh.koonol_management.data.models.SellerCreateEditModel
import com.fernandokh.koonol_management.data.models.SellerModel
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.utils.evaluateHttpException
import com.fernandokh.koonol_management.utils.formatIsoDateToDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EditSellerViewModel : ViewModel() {
    val optionsGender = listOf(
        SelectOption("Selecciona un género", ""),
        SelectOption("Masculino", "male"),
        SelectOption("Fenemino", "female"),
        SelectOption("Otro", "other")
    )
    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")

    private val apiService = RetrofitInstance.create(SellerApiService::class.java)

    private val _isSeller = MutableStateFlow<SellerModel?>(null)
    val isSeller: StateFlow<SellerModel?> = _isSeller

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

    private val _dayOfBirth = MutableStateFlow<String?>(null)
    val dayOfBirth: StateFlow<String?> = _dayOfBirth

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

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

    fun onPhotoChange(value: String?) {
        _isPhoto.value = value
    }


    fun dismissDialog() {
        _isShowDialog.value = false
    }

    fun showDialog() {
        _isShowDialog.value = true
    }

    fun getSeller(sellerId: String?) {

        if (sellerId == null) {
            _isSeller.value = null
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.getSellerById(sellerId)
                _isSeller.value = response.data
                _isName.value = response.data?.name ?: ""
                _lastName.value = response.data?.lastName ?: ""
                _email.value = response.data?.email ?: ""
                _isPhoto.value = response.data?.photo
                _dayOfBirth.value = response.data?.birthday?.let { formatIsoDateToDate(it) }
                _phone.value = response.data?.phoneNumber ?: ""
                _gender.value =
                    optionsGender.find { it.value == response.data?.gender } ?: optionsGender[0]
                Log.i("dev-debug", "Vendedor obtenido con éxito: $sellerId")
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error al obtener el vendedor: $errorMessage")
                _isSeller.value = null
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                _isSeller.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSeller() {
        val seller = SellerCreateEditModel(
            name = _isName.value.trim(),
            email = _email.value.trim().ifBlank { null },
            lastName = _lastName.value.trim(),
            photo = _isPhoto.value,
            phoneNumber = _phone.value.ifBlank { null },
            gender = _gender.value.value,
            birthday = _dayOfBirth.value!!
        )
        Log.i("dev-debug", seller.toString())

        viewModelScope.launch {
            try {
                _isLoadingUpdate.value = true
                apiService.updateSeller(_isSeller.value?.id ?: "", seller)
                showToast("Vendedor actualizado con éxito")
                _navigationEvent.send(NavigationEvent.Navigate)
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error al editar el vendedor: $errorMessage")
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrio un error al actualizar el vendedor")
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
        if (!emailPattern.matches(email) && email.isNotBlank()) {
            _formErrors.value =
                _formErrors.value.copy(emailError = "Ingresa el correo electrónico válido")
        } else {
            _formErrors.value = _formErrors.value.copy(emailError = null)
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
        if (phoneNumber.length in 1..9) {
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
        validateEmail()
        validateGender()
        validateDayOfBirth()
        validatePhoneNumber()

        return _formErrors.value.allErrors().all { it === null }
    }
}