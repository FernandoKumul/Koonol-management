package com.fernandokh.koonol_management.viewModel.tianguis

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.TianguisApiService
import com.fernandokh.koonol_management.data.models.TianguisCreateEditModel
import com.fernandokh.koonol_management.data.models.MarkerMap
import com.fernandokh.koonol_management.data.models.ScheduleCreateModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class TianguisFormErrors(
    val nameError: String? = null,
    val colorError: String? = null,
    val dayWeekError: String? = null,
    val indicationsError: String? = null,
    val startTimeError: String? = null,
    val endTimeError: String? = null,
    val localityError: String? = null,
    val photoError: String? = null
) {
    fun allErrors(): List<String?> {
        return listOf(
            nameError,
            colorError,
            dayWeekError,
            indicationsError,
            startTimeError,
            endTimeError,
            localityError,
            photoError
        )
    }
}

class CreateTianguisViewModel : ViewModel() {
    private val apiService = RetrofitInstance.create(TianguisApiService::class.java)

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _isLoadingCreate = MutableStateFlow(false)
    val isLoadingCreate: StateFlow<Boolean> = _isLoadingCreate

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _color = MutableStateFlow("")
    val color: StateFlow<String> = _color

    private val _dayWeek = MutableStateFlow("")
    val dayWeek: StateFlow<String> = _dayWeek

    private val _indications = MutableStateFlow("")
    val indications: StateFlow<String> = _indications

    private val _startTime = MutableStateFlow("")
    val startTime: StateFlow<String> = _startTime

    private val _endTime = MutableStateFlow("")
    val endTime: StateFlow<String> = _endTime

    private val _locality = MutableStateFlow("")
    val locality: StateFlow<String> = _locality

    private val _latitude = MutableStateFlow(19.4326) // Coordenada inicial (CDMX)
    val latitude: StateFlow<Double> = _latitude

    private val _longitude = MutableStateFlow(-99.1332) // Coordenada inicial (CDMX)
    val longitude: StateFlow<Double> = _longitude

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _photo = MutableStateFlow<String?>(null)
    val photo: StateFlow<String?> = _photo

    private val _formErrors = MutableStateFlow(TianguisFormErrors())
    val formErrors: StateFlow<TianguisFormErrors> = _formErrors

    private val _dirtyForm = MutableStateFlow(false)

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun updateCoordinates(lat: Double, lng: Double) {
        _latitude.value = lat
        _longitude.value = lng
    }

    fun onNameChange(value: String) {
        _name.value = value
        if (_dirtyForm.value) validateName()
    }

    fun onColorChange(value: String) {
        _color.value = value
        if (_dirtyForm.value) validateColor()
    }

    fun onDayWeekChange(value: String) {
        _dayWeek.value = value
        if (_dirtyForm.value) validateDayWeek()
    }

    fun onIndicationsChange(value: String) {
        _indications.value = value
        if (_dirtyForm.value) validateIndications()
    }

    fun onStartTimeChange(value: String) {
        _startTime.value = value
        if (_dirtyForm.value) validateStartTime()
    }

    fun onEndTimeChange(value: String) {
        _endTime.value = value
        if (_dirtyForm.value) validateEndTime()
    }

    fun onLocalityChange(value: String) {
        _locality.value = value
        if (_dirtyForm.value) validateLocality()
    }

    fun onPhotoChange(value: String?) {
        _photo.value = value
        if (_dirtyForm.value) validatePhoto()
    }

    fun dismissDialog() {
        _isShowDialog.value = false
    }

    fun showDialog() {
        _isShowDialog.value = true
    }


    fun createTianguis(userId: String) {
        val tianguis = TianguisCreateEditModel(
            userId = userId, // Cambia esto a tu lógica de usuario
            name = _name.value.trim(),
            color = _color.value.trim(),
            schedule = ScheduleCreateModel(
                dayWeek = _dayWeek.value.trim(),
                startTime = _startTime.value.trim(),
                endTime = _endTime.value.trim(),
            ),
            photo = _photo.value,
            indications = _indications.value.trim(),
            locality = _locality.value.trim(),
            active = true,
            markerMap = MarkerMap(
                type = "Point",
                coordinates = listOf(_longitude.value, _latitude.value) // Coordenadas seleccionadas
            )
        )

        viewModelScope.launch {
            try {
                _isLoadingCreate.value = true
                apiService.createTianguis(tianguis)
                showToast("Tianguis creado con éxito")
                _navigationEvent.send(NavigationEvent.TianguisCreated)
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                showToast(errorMessage)
            } catch (e: Exception) {
                showToast("Error al crear el tianguis: ${e.message}")
                Log.e("dev-debug", "${e.message}")
            } finally {
                _isLoadingCreate.value = false
                dismissDialog()
            }
        }
    }

    private fun validateName() {
        val name = _name.value
        _formErrors.value = _formErrors.value.copy(
            nameError = if (name.isBlank()) "El nombre es requerido" else null
        )
    }

    private fun validateColor() {
        val color = _color.value
        _formErrors.value = _formErrors.value.copy(
            colorError = if (color.isBlank()) "El color es requerido" else null
        )
    }

    private fun validateDayWeek() {
        val dayWeek = _dayWeek.value
        _formErrors.value = _formErrors.value.copy(
            dayWeekError = if (dayWeek.isBlank()) "El día de la semana es requerido" else null
        )
    }

    private fun validateIndications() {
        val indications = _indications.value
        _formErrors.value = _formErrors.value.copy(
            indicationsError = if (indications.isBlank()) "Las indicaciones son requeridas" else null
        )
    }

    private fun validateStartTime() {
        val startTime = _startTime.value
        _formErrors.value = _formErrors.value.copy(
            startTimeError = if (startTime.isBlank()) "La hora de inicio es requerida" else null
        )
    }

    private fun validateEndTime() {
        val endTime = _endTime.value
        _formErrors.value = _formErrors.value.copy(
            endTimeError = if (endTime.isBlank()) "La hora de finalización es requerida" else null
        )
    }

    private fun validateLocality() {
        val locality = _locality.value
        _formErrors.value = _formErrors.value.copy(
            localityError = if (locality.isBlank()) "La localidad es requerida" else null
        )
    }

    private fun validatePhoto() {
        val photo = _photo.value
        _formErrors.value = _formErrors.value.copy(
            photoError = if (photo != null && !photo.startsWith("http")) "Debe ser una URL válida" else null
        )
    }

    fun isFormValid(): Boolean {
        _dirtyForm.value = true
        validateName()
        validateColor()
        validateDayWeek()
        validateIndications()
        validateStartTime()
        validateEndTime()
        validateLocality()
        validatePhoto()

        return _formErrors.value.allErrors().all { it == null }
    }
}

sealed class NavigationEvent {
    object TianguisCreated : NavigationEvent()
}
