package com.fernandokh.koonol_management.viewModel.salesstalls

import androidx.lifecycle.ViewModel
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.SalesStallsApiService
import com.fernandokh.koonol_management.utils.NavigationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

data class FormErrors(
    val sellerIdError: String? = null,
    val subCategoryIdError: String? = null,
    val nameError: String? = null,
    val principalPhotoError: String? = null,
    val descriptionError: String? = null,
    val typeError: String? = null,
    val probationError: String? = null,
    val activeError: String? = null,
) {
    fun allErrors(): List<String?> {
        return listOf(
            sellerIdError,
            subCategoryIdError,
            nameError,
            principalPhotoError,
            descriptionError,
            typeError,
            probationError,
            activeError
        )
    }
}

class CreateSaleStallViewModel : ViewModel() {
    private val apiService = RetrofitInstance.create(SalesStallsApiService::class.java)

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _isLoadingCreate = MutableStateFlow(false)
    val isLoadingCreate: StateFlow<Boolean> = _isLoadingCreate

    private val _isPrincipalPhoto = MutableStateFlow<String>("")
    val isPrincipalPhoto: StateFlow<String> = _isPrincipalPhoto

    private val _isSecondPhoto = MutableStateFlow<String?>(null)
    val isSecondPhoto: StateFlow<String?> = _isSecondPhoto

    private val _isThirdPhoto = MutableStateFlow<String?>(null)
    val isThirdPhoto: StateFlow<String?> = _isThirdPhoto

    private val _isName = MutableStateFlow("")
    val isName: StateFlow<String> = _isName

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _type = MutableStateFlow("")
    val type: StateFlow<String> = _type

    private val _probation = MutableStateFlow(false)
    val probation: StateFlow<Boolean> = _probation

    private val _active = MutableStateFlow(false)
    val active: StateFlow<Boolean> = _active

    private val _sellerId = MutableStateFlow("")
    val sellerId: StateFlow<String> = _sellerId

    private val _subCategoryId = MutableStateFlow("")
    val subCategoryId: StateFlow<String> = _subCategoryId

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

    fun onDescriptionChange(value: String) {
        _description.value = value
        if (_dirtyForm.value) {
            validateDescription()
        }
    }

    private fun validateSellerId() {
        val sellerId = _sellerId.value
        if (sellerId.isBlank()) {
            _formErrors.value =
                _formErrors.value.copy(sellerIdError = "El vendedor es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(sellerIdError = null)
        }
    }

    private fun validateSubCategoryId() {
        val subCategoryId = _subCategoryId.value
        if (subCategoryId.isBlank()) {
            _formErrors.value =
                _formErrors.value.copy(subCategoryIdError = "La subcategoría es requerida")
        } else {
            _formErrors.value = _formErrors.value.copy(subCategoryIdError = null)
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

    private fun validatePrincipalPhoto() {
        val photo = _isPrincipalPhoto.value
        if (photo.isBlank()) {
            _formErrors.value =
                _formErrors.value.copy(principalPhotoError = "La foto principal es requerida")
        } else {
            _formErrors.value = _formErrors.value.copy(principalPhotoError = null)
        }
    }

    private fun validateDescription() {
        val description = _description.value
        if (description.isBlank()) {
            _formErrors.value =
                _formErrors.value.copy(descriptionError = "La descripción es requerida")
        } else {
            _formErrors.value = _formErrors.value.copy(descriptionError = null)
        }
    }

    private fun validateType() {
        val type = _type.value
        if (type.isBlank()) {
            _formErrors.value =
                _formErrors.value.copy(typeError = "El tipo de puesto es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(typeError = null)
        }
    }

    private fun validateProbation() {
        val probation = _probation.value
        if (probation) {
            _formErrors.value =
                _formErrors.value.copy(probationError = "El periodo de prueba es requerida")
        } else {
            _formErrors.value = _formErrors.value.copy(probationError = null)
        }
    }

    private fun validateActive() {
        val active = _active.value
        if (active) {
            _formErrors.value =
                _formErrors.value.copy(activeError = "El estado del puesto es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(activeError = null)
        }
    }
}