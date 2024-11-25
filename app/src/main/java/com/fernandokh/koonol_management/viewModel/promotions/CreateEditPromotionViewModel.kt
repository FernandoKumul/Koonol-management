package com.fernandokh.koonol_management.viewModel.promotions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.PromotionApiService
import com.fernandokh.koonol_management.data.api.SalesStallsApiService
import com.fernandokh.koonol_management.data.models.PromotionAndNameModel
import com.fernandokh.koonol_management.data.models.PromotionOutModel
import com.fernandokh.koonol_management.data.models.SalesStallOnlyNameModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CreateEditPromotionViewModelFactory(private val tokenManager: TokenManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateEditPromotionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateEditPromotionViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CreateEditPromotionViewModel(private val tokenManager: TokenManager) : ViewModel() {
    private val apiService = RetrofitInstance.create(PromotionApiService::class.java)
    private val apiServiceSalesStall = RetrofitInstance.create(SalesStallsApiService::class.java)
    private val _accessToken = MutableStateFlow("")

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    data class FormErrors(
        val salesStallIdOption: String? = null,
        val startDate: String? = null,
        val endDate: String? = null,
        val pay: String? = null,
    ) {
        fun allErrors(): List<String?> {
            return listOf(
                salesStallIdOption,
                startDate,
                endDate,
                pay
            )
        }
    }

    val optionsSalesStall = listOf(
        SelectOption("Selecciona un puesto", ""),
    )

    data class FormPromotion(
        val salesStallIdOption: SelectOption,
        val startDate: String? = null,
        val endDate: String? = null,
        val pay: String = "0"
    )

    private val _isSalesStallOptions = MutableStateFlow(optionsSalesStall)
    val isSalesStallOptions: StateFlow<List<SelectOption>> = _isSalesStallOptions

    private val _form = MutableStateFlow(FormPromotion(salesStallIdOption = optionsSalesStall[0]))
    val form: StateFlow<FormPromotion> = _form

    private val _formErrors = MutableStateFlow(FormErrors())
    val formErrors: StateFlow<FormErrors> = _formErrors

    private val _dirtyForm = MutableStateFlow(false)

    private val _isPromotion = MutableStateFlow<PromotionAndNameModel?>(null)
    val isPromotion: StateFlow<PromotionAndNameModel?> = _isPromotion

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingSalesStall = MutableStateFlow(true)
    val isLoadingSalesStall: StateFlow<Boolean> = _isLoadingSalesStall

    private val _isLoadingCreate = MutableStateFlow(false)
    val isLoadingCreate: StateFlow<Boolean> = _isLoadingCreate

    private val _isLoadingEdit = MutableStateFlow(false)
    val isLoadingEdit: StateFlow<Boolean> = _isLoadingEdit

    init {
        viewModelScope.launch {
            val savedToken = tokenManager.accessToken.first()
            _accessToken.value = savedToken
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

    fun showDialog() {
        _isShowDialog.value = true
    }

    fun createPromotion() {
        viewModelScope.launch {
            try {
                _isLoadingCreate.value = true

                val promotion = PromotionOutModel(
                    salesStallId = _form.value.salesStallIdOption.value,
                    startDate = _form.value.startDate!!,
                    endDate = _form.value.endDate!!,
                    pay = _form.value.pay.toDouble()
                )
                apiService.create(
                    "Bearer ${_accessToken.value}",
                    promotion
                )
                showToast("Promoción creada con éxito")
                _navigationEvent.send(NavigationEvent.Navigate)
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error api: $errorMessage")
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrio un error al crear la Promoción")
            } finally {
                _isLoadingCreate.value = false
                dismissDialog()
            }
        }
    }

    fun updatePromotion() {
        viewModelScope.launch {
            try {
                val promotion = PromotionOutModel(
                    salesStallId = _form.value.salesStallIdOption.value,
                    startDate = _form.value.startDate!!,
                    endDate = _form.value.endDate!!,
                    pay = _form.value.pay.toDouble()
                )

                _isLoadingEdit.value = true
                apiService.update(
                    "Bearer ${_accessToken.value}",
                    _isPromotion.value?.id ?: "",
                    promotion
                )
                showToast("Promoción actualizada con éxito")
                Log.i("dev-debug", "Antes")
                _navigationEvent.send(NavigationEvent.Navigate)
                Log.i("dev-debug", "Despues")
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error api: $errorMessage")
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrio un error al actualizar la Promoción")
            } finally {
                _isLoadingEdit.value = false
                dismissDialog()
            }
        }
    }

    fun getPromotion(accessToken: String, promotionId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response =
                    apiService.getById("Bearer $accessToken", promotionId)
                _isPromotion.value = response.data

                if (_isPromotion.value != null) {
                    val salesStallMatch = optionsSalesStall.find { it.value == _isPromotion.value?.salesStallId?.id }

                    _form.value = FormPromotion(
                        salesStallIdOption = salesStallMatch ?: optionsSalesStall[0],
                        startDate = _isPromotion.value?.startDate,
                        endDate = _isPromotion.value?.endDate,
                        pay = _isPromotion.value?.pay.toString()
                    )
                }
                Log.i("dev-debug", "Promoción obtenida con éxito")
            } catch (e: HttpException) {
                val messageError = evaluateHttpException(e)
                Log.e("dev-debug", "Error al obtener la categoría: $messageError")
                _isPromotion.value = null
            } catch (e: Exception) {
                Log.e("dev-debug", e.message ?: "Ha ocurrido un error")
                _isPromotion.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getSalesStall(accessToken: String = _accessToken.value) {
        viewModelScope.launch {
            try {
                _isLoadingSalesStall.value = true
                val response =
                    apiServiceSalesStall.getSalesStallOnlyNames("Bearer $accessToken")

                if (response.data != null) addSalesStalls(response.data)
                Log.i("dev-debug", "Puestos obtenidos con éxito")
            } catch (e: HttpException) {
                val messageError = evaluateHttpException(e)
                Log.e("dev-debug", "Error al obtener los puestos: $messageError")
                showToast(messageError)
            } catch (e: Exception) {
                Log.e("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Error al obtener los puestos")
            } finally {
                _isLoadingSalesStall.value = false
            }
        }
    }

    private fun addSalesStalls(data: List<SalesStallOnlyNameModel>) {
        val newOptions = data.map { SelectOption(it.name, it.id) }
        Log.i("dev-debug", _isSalesStallOptions.value.toString())
        _isSalesStallOptions.update { it + newOptions }
        Log.i("dev-debug", _isSalesStallOptions.value.toString())

    }

    fun onSalesStallOptionChange(value: SelectOption) {
        _form.value = _form.value.copy(salesStallIdOption = value)

        if (_dirtyForm.value) {
            validateSalesStall()
        }
    }

    fun onStartDateChange(value: String?) {
        _form.value = _form.value.copy(startDate = value)

        if (_dirtyForm.value) {
            validateStartDate()
        }
    }

    fun onEndDateChange(value: String?) {
        _form.value = _form.value.copy(endDate = value)

        if (_dirtyForm.value) {
            validateEndDate()
        }
    }

    fun onPayChange(value: String) {
        _form.value = _form.value.copy(pay = value)

        if (_dirtyForm.value) {
            validatePay()
        }
    }

    private fun validatePay() {
        val rate = _form.value.pay

        try {
            val payDouble = rate.toDouble()
            if (payDouble <= 0) {
                _formErrors.value =
                    _formErrors.value.copy(pay = "La promoción debe de ser mayor que 0")
            } else {
                _formErrors.value = _formErrors.value.copy(pay = null)
            }
        } catch (e: Exception) {
            _formErrors.value =
                _formErrors.value.copy(pay = "La promoción no es número válido")
        }
    }

    private fun validateSalesStall() {
        val id = _form.value.salesStallIdOption.value
        if (id == "") {
            _formErrors.value = _formErrors.value.copy(salesStallIdOption = "El puesto es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(salesStallIdOption = null)
        }
    }

    private fun validateStartDate() {
        val startDate = _form.value.startDate
        if (startDate == null) {
            _formErrors.value = _formErrors.value.copy(startDate = "La fecha de inicio es requerida")
        } else {
            _formErrors.value = _formErrors.value.copy(startDate = null)
        }
    }

    private fun validateEndDate() {
        val date = _form.value.endDate
        if (date == null) {
            _formErrors.value = _formErrors.value.copy(endDate = "La fecha de finalización es requerida")
        } else {
            _formErrors.value = _formErrors.value.copy(endDate = null)
        }
    }

    fun isFormValid(): Boolean {
        _dirtyForm.value = true
        validatePay()
        validateEndDate()
        validateStartDate()
        validateSalesStall()

        return _formErrors.value.allErrors().all { it === null }
    }
}