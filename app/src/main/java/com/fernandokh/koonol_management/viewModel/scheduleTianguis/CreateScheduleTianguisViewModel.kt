package com.fernandokh.koonol_management.viewModel.scheduleTianguis

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.ScheduleTianguisApiService
import com.fernandokh.koonol_management.data.api.TianguisApiService
import com.fernandokh.koonol_management.data.models.ScheduleTianguisCreateEditModel
import com.fernandokh.koonol_management.data.models.TianguisModel
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
    val value: String
)

data class FormErrors(
    val tianguisIdError: String? = null,
    val dayWeekError: String? = null,
    val startTimeError: String? = null,
    val endTimeError: String? = null,
) {
    fun allErrors(): List<String?> {
        return listOf(
            tianguisIdError,
            dayWeekError,
            startTimeError,
            endTimeError
        )
    }
}

class CreateScheduleTianguisViewModel : ViewModel() {
    private val apiService = RetrofitInstance.create(ScheduleTianguisApiService::class.java)
    private val tianguisApiService = RetrofitInstance.create(TianguisApiService::class.java)

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _isLoadingCreate = MutableStateFlow(false)
    val isLoadingCreate: StateFlow<Boolean> = _isLoadingCreate

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _tianguisId = MutableStateFlow("")
    val tianguisId: StateFlow<String> = _tianguisId

    private val _dayWeek = MutableStateFlow<String?>(null)
    val dayWeek: StateFlow<String?> = _dayWeek

    private val _startTime = MutableStateFlow("")
    val startTime: StateFlow<String> = _startTime

    private val _endTime = MutableStateFlow("")
    val endTime: StateFlow<String> = _endTime

    private val _formErrors = MutableStateFlow(FormErrors())
    val formErrors: StateFlow<FormErrors> = _formErrors

    private val _dirtyForm = MutableStateFlow(false)

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _tianguisList = MutableStateFlow<List<TianguisModel>>(emptyList())
    val tianguisList: StateFlow<List<TianguisModel>> = _tianguisList

    val daysOfWeek = listOf(
        Option("Lunes", "Lunes"),
        Option("Martes", "Martes"),
        Option("Miércoles", "Miércoles"),
        Option("Jueves", "Jueves"),
        Option("Viernes", "Viernes"),
    )

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

    fun onTianguisIdChange(value: String) {
        _tianguisId.value = value
        if (_dirtyForm.value) {
            validateTianguisId()
        }
    }

    fun onDayWeekChange(value: String?) {
        _dayWeek.value = value
        if (_dirtyForm.value) {
            validateDayWeek()
        }
    }

    fun onStartTimeChange(value: String) {
        _startTime.value = value
        if (_dirtyForm.value) {
            validateStartTime()
        }
    }

    fun onEndTimeChange(value: String) {
        _endTime.value = value
        if (_dirtyForm.value) {
            validateEndTime()
        }
    }

    fun createScheduleTianguis() {
        if (_dayWeek.value == null) {
            return
        }
        val scheduleTianguis = ScheduleTianguisCreateEditModel(
            tianguisId = _tianguisId.value.trim(),
            dayWeek = _dayWeek.value!!,
            startTime = _startTime.value.trim(),
            endTime = _endTime.value.trim()
        )

        viewModelScope.launch {
            try {
                _isLoadingCreate.value = true
                apiService.createScheduleTianguis(scheduleTianguis)
                showToast("Horario agregado con éxito")
                _navigationEvent.send(NavigationEvent.Navigate)
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrio un error al crear el horario para el tianguis")
            } finally {
                _isLoadingCreate.value = false
                dismissDialog()
            }
        }
    }

    fun getAllTianguis() {
        viewModelScope.launch {
            try {
                val response = tianguisApiService.getAllTianguis()
                if (response.success) {
                    Log.i("dev-debug", "Lista obtenida con éxito")
                    _tianguisList.value = response.data!!
                } else {
                    showToast("No se encontraron vendedores")
                }
                _isLoading.value = false
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ah ocurrido un error")
                showToast("Ocurrio un error al obtener los tianguis")
            }
        }
    }

    private fun validateTianguisId() {
        val tianguisId = _tianguisId.value
        if (tianguisId.isBlank()) {
            _formErrors.value =
                _formErrors.value.copy(tianguisIdError = "El ID del tianguis es requerido")
        }
    }

    private fun validateDayWeek() {
        val dayWeek = _dayWeek.value
        if (dayWeek == null) {
            _formErrors.value =
                _formErrors.value.copy(dayWeekError = "El día de la semana es requerido")
        }
    }

    private fun validateStartTime() {
        val startTime = _startTime.value
        if (startTime.isBlank()) {
            _formErrors.value =
                _formErrors.value.copy(startTimeError = "La hora de inicio es requerida")
        } else if (!isValidTimeFormat(startTime)) {
            _formErrors.value =
                _formErrors.value.copy(startTimeError = "La hora de inicio debe ser un formato valido (24:59)")
        } else {
            _formErrors.value = _formErrors.value.copy(startTimeError = null)
        }
    }

    private fun validateEndTime() {
        val endTime = _endTime.value
        if (endTime.isBlank()) {
            _formErrors.value =
                _formErrors.value.copy(endTimeError = "La hora de finalización es requerida")
        } else if (!isValidTimeFormat(endTime)) {
            _formErrors.value =
                _formErrors.value.copy(endTimeError = "La hora de finalización debe ser un formato valido (24:59)")
        } else {
            _formErrors.value = _formErrors.value.copy(endTimeError = null)
        }
    }

    private fun isValidTimeFormat(time: String): Boolean {
        val regex = Regex("^([01]?\\d|2[0-3]):([0-5]\\d)\$")
        return regex.matches(time)
    }

    fun isFormValid(): Boolean {
        _dirtyForm.value = true
        validateTianguisId()
        validateDayWeek()
        validateStartTime()
        validateEndTime()

        return _formErrors.value.allErrors().all { it === null }
    }

}