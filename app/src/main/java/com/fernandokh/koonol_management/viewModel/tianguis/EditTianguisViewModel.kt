package com.fernandokh.koonol_management.viewModel.tianguis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.TianguisApiService
import com.fernandokh.koonol_management.data.models.MarkerMap
import com.fernandokh.koonol_management.data.models.TianguisCreateEditModel
import com.fernandokh.koonol_management.data.models.TianguisModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import com.fernandokh.koonol_management.viewModel.users.FormErrors
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import android.util.Log
import com.fernandokh.koonol_management.data.models.ScheduleEditModel
import com.fernandokh.koonol_management.data.models.TianguisEditModel

class EditTianguisViewModel : ViewModel() {

    private val apiService = RetrofitInstance.create(TianguisApiService::class.java)

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingUpdate = MutableStateFlow(false)
    val isLoadingUpdate: StateFlow<Boolean> = _isLoadingUpdate

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _color = MutableStateFlow("")
    val color: StateFlow<String> = _color

    private val _dayWeek = MutableStateFlow("")
    val dayWeek: StateFlow<String> = _dayWeek

    private val _photo = MutableStateFlow<String?>(null)
    val photo: StateFlow<String?> = _photo

    private val _indications = MutableStateFlow("")
    val indications: StateFlow<String> = _indications

    private val _startTime = MutableStateFlow("")
    val startTime: StateFlow<String> = _startTime

    private val _endTime = MutableStateFlow("")
    val endTime: StateFlow<String> = _endTime

    private val _locality = MutableStateFlow("")
    val locality: StateFlow<String> = _locality

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _tianguis = MutableStateFlow<TianguisModel?>(null)
    val tianguis: StateFlow<TianguisModel?> = _tianguis

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _formErrors = MutableStateFlow(TianguisFormErrors())
    val formErrors: StateFlow<TianguisFormErrors> = _formErrors

    private val _latitude = MutableStateFlow(19.4326) // Coordenada inicial (CDMX)
    val latitude: StateFlow<Double> = _latitude

    private val _longitude = MutableStateFlow(-99.1332) // Coordenada inicial (CDMX)
    val longitude: StateFlow<Double> = _longitude

    private val _dirtyForm = MutableStateFlow(false)

    private fun showToast(message: String) {
        Log.d("EditTianguisViewModel", "Showing toast message: $message")
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        Log.d("EditTianguisViewModel", "Resetting toast message")
        _toastMessage.value = null
    }

    fun onNameChange(value: String) {
        Log.d("EditTianguisViewModel", "Name changed: $value")
        _name.value = value
    }

    fun onColorChange(value: String) {
        Log.d("EditTianguisViewModel", "Color changed: $value")
        _color.value = value
    }

    fun onDayWeekChange(value: String) {
        Log.d("EditTianguisViewModel", "Day of the week changed: $value")
        _dayWeek.value = value
    }

    fun onPhotoChange(value: String?) {
        Log.d("EditTianguisViewModel", "Photo changed: $value")
        _photo.value = value
    }

    fun onIndicationsChange(value: String) {
        Log.d("EditTianguisViewModel", "Indications changed: $value")
        _indications.value = value
    }

    fun onStartTimeChange(value: String) {
        Log.d("EditTianguisViewModel", "Start time changed: $value")
        _startTime.value = value
    }

    fun onEndTimeChange(value: String) {
        Log.d("EditTianguisViewModel", "End time changed: $value")
        _endTime.value = value
    }

    fun onLocalityChange(value: String) {
        Log.d("EditTianguisViewModel", "Locality changed: $value")
        _locality.value = value
        if (_dirtyForm.value) validateLocality()
    }

    fun updateCoordinates(lat: Double, lng: Double) {
        _latitude.value = lat
        _longitude.value = lng
    }

    fun dismissDialog() {
        Log.d("EditTianguisViewModel", "Dismissing dialog")
        _isShowDialog.value = false
    }

    fun showDialog() {
        Log.d("EditTianguisViewModel", "Showing dialog")
        _isShowDialog.value = true
    }

    fun getTianguis(tianguisId: String?) {
        if (tianguisId == null) {
            Log.w("EditTianguisViewModel", "Tianguis ID is null")
            _tianguis.value = null
            return
        }

        viewModelScope.launch {
            try {
                Log.d("EditTianguisViewModel", "Fetching Tianguis with ID: $tianguisId")
                _isLoading.value = true
                val response = apiService.getTianguisById(tianguisId)
                _tianguis.value = response.data

                Log.d("EditTianguisViewModel", "Tianguis fetched successfully: ${response.data}")

                // Cargar los valores en los campos
                _name.value = response.data?.name ?: ""
                _color.value = response.data?.color ?: ""
                _dayWeek.value = response.data?.schedule?.get(0)?.dayWeek ?: ""
                _photo.value = response.data?.photo
                _indications.value = response.data?.indications ?: ""
                _startTime.value = response.data?.schedule?.get(0)?.startTime ?: ""
                _endTime.value = response.data?.schedule?.get(0)?.endTime ?: ""
                _locality.value = response.data?.locality ?: ""
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("EditTianguisViewModel", "HTTP error: $errorMessage", e)
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.e("EditTianguisViewModel", "Error fetching Tianguis: ${e.message}", e)
                showToast("Error al obtener el tianguis: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTianguis(tianguisId: String, userId: String) {
        val updatedTianguis = TianguisEditModel(
            userId = userId, // Cambia esto según la lógica de usuario
            name = _name.value.trim(),
            color = _color.value.trim(),
            schedule = ScheduleEditModel(
                id = _tianguis.value?.schedule?.get(0)?.id ?: "",
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
                coordinates = listOf(
                    _longitude.value ?: -99.1332, // Longitude (requerido)
                    _latitude.value ?: 19.4326,   // Latitude (requerido)
                )
            )
        )

        Log.d("EditTianguisViewModel", "Payload enviado para actualizar Tianguis: $updatedTianguis")


        viewModelScope.launch {
            try {
                _isLoadingUpdate.value = true
                apiService.updateTianguis(tianguisId, updatedTianguis)
                showToast("Tianguis actualizado con éxito")
                _navigationEvent.send(NavigationEvent.TianguisCreated)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = evaluateHttpException(e)
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.e("EditTianguisViewModel", "Error updating Tianguis: ${e.message}", e)
                showToast("Error al actualizar el tianguis: ${e.message}")
            } finally {
                _isLoadingUpdate.value = false
                dismissDialog()
            }
        }
    }

    // Validaciones individuales
    private fun validateName() {
        val name = _name.value
        Log.d("EditTianguisViewModel", "Validating name: $name")
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
        _formErrors.value = _formErrors.value.copy(dayWeekError = if (dayWeek.isBlank()) "El día de la semana es requerido" else null
        )
    }

    private fun validatePhoto() {
        val photo = _photo.value
        _formErrors.value = _formErrors.value.copy(
            photoError = if (photo != null && !photo.startsWith("http")) "Debe ser una URL válida" else null
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

    // Método para verificar si el formulario es válido
    fun isFormValid(): Boolean {
        Log.d("EditTianguisViewModel", "Validating form")
        _dirtyForm.value = true
        validateName()
        validateColor()
        validateDayWeek()
        validatePhoto()
        validateIndications()
        validateStartTime()
        validateEndTime()
        validateLocality()

        val isValid = _formErrors.value.allErrors().all { it == null }
        Log.d("EditTianguisViewModel", "Form validation result: $isValid")
        return isValid
    }
}
