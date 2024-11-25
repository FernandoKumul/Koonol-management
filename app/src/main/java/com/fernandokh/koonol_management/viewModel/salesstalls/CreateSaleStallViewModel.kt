package com.fernandokh.koonol_management.viewModel.salesstalls

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.SalesStallsApiService
import com.fernandokh.koonol_management.data.models.SaleStallCreateEditModel
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class Option(
    val name: String,
    val value: Boolean
)

data class FormErrors(
    val sellerIdError: String? = null,
    val subCategoryIdError: String? = null,
    val nameError: String? = null,
    val descriptionError: String? = null,
    val typeError: String? = null,
) {
    fun allErrors(): List<String?> {
        return listOf(
            sellerIdError,
            subCategoryIdError,
            nameError,
            descriptionError,
            typeError,
        )
    }
}

class CreateSaleStallViewModel : ViewModel() {
    private val apiService = RetrofitInstance.create(SalesStallsApiService::class.java)

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _isLoadingCreate = MutableStateFlow(false)
    val isLoadingCreate: StateFlow<Boolean> = _isLoadingCreate

    private val _isPrincipalPhoto = MutableStateFlow<String?>(null)
    val isPrincipalPhoto: StateFlow<String?> = _isPrincipalPhoto

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

    private val photosList = ArrayList<String>()

    private fun managePhotosList() {
        photosList.clear()
        isPrincipalPhoto.value?.let { photosList.add(it) }
        isSecondPhoto.value?.let { photosList.add(it) }
        isThirdPhoto.value?.let { photosList.add(it) }
    }


    val probationOptions = listOf(
        Option("Sí", true),
        Option("No", false)
    )

    val activeOptions = listOf(
        Option("Activo", true),
        Option("Inactivo", false)
    )


    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun onSellerIdChange(value: String) {
        _sellerId.value = value
        if (_dirtyForm.value) {
            validateSellerId()
        }
    }
    fun onSubCategoryIdChange(value: String) {
        _subCategoryId.value = value
        if (_dirtyForm.value) {
            validateSubCategoryId()
        }
    }

    fun onNameChange(value: String) {
        _isName.value = value
        if (_dirtyForm.value) {
            validateName()
        }
    }

    fun onPrincipalPhotoChange(value: String?) {
        _isPrincipalPhoto.value = value
    }

    fun onSecondPhotoChange(value: String?) {
        _isSecondPhoto.value = value
    }

    fun onThirdPhotoChange(value: String?) {
        _isThirdPhoto.value = value
    }

    fun onDescriptionChange(value: String) {
        _description.value = value
        if (_dirtyForm.value) {
            validateDescription()
        }
    }

    fun onTypeChange(value: String) {
        _type.value = value
        if (_dirtyForm.value) {
            validateType()
        }
    }

    fun onProbationChange(value: Boolean) {
        _probation.value = value
    }

    fun onActiveChange(value: Boolean) {
        _active.value = value
    }

    fun dismissDialog() {
        _isShowDialog.value = false
    }

    fun showDialog() {
        _isShowDialog.value = true
    }

    fun createSaleStall() {
        managePhotosList()

        val saleStall = SaleStallCreateEditModel(
            sellerId = _sellerId.value,
            subCategoryId = _subCategoryId.value,
            name = _isName.value,
            photos = photosList,
            description = _description.value,
            type = _type.value,
            probation = _probation.value,
            active = _active.value
        )

        viewModelScope.launch {
            try {
                _isLoadingCreate.value = true
                apiService.createSalesStalls(saleStall)
                showToast("Puesto agregado con éxito")
                _navigationEvent.send(NavigationEvent.Navigate)
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrio un error al crear el puesto")
            } finally {
                _isLoadingCreate.value = false
                dismissDialog()
            }
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

    fun isFormValid(): Boolean {
        validateSellerId()
        validateSubCategoryId()
        validateName()
        validateDescription()
        validateType()
        return _formErrors.value.allErrors().all { it == null }
    }

}
