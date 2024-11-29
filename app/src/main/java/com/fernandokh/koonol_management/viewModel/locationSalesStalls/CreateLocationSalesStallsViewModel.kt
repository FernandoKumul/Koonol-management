package com.fernandokh.koonol_management.viewModel.locationSalesStalls

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.LocationSalesStallsApiService
import com.fernandokh.koonol_management.data.api.ScheduleTianguisApiService
import com.fernandokh.koonol_management.data.api.TianguisApiService
import com.fernandokh.koonol_management.data.models.LocationSalesStallsCreateEditModel
import com.fernandokh.koonol_management.data.models.MarkerMap
import com.fernandokh.koonol_management.data.models.ScheduleTianguisModel
import com.fernandokh.koonol_management.data.models.TianguisModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import com.fernandokh.koonol_management.viewModel.tianguis.NavigationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CreateLocationSalesStallsViewModel : ViewModel() {
    private val apiService = RetrofitInstance.create(LocationSalesStallsApiService::class.java)
    private val tianguisApiService = RetrofitInstance.create(TianguisApiService::class.java)
    private val scheduleTianguisApiService = RetrofitInstance.create(ScheduleTianguisApiService::class.java)

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _isLoadingCreate = MutableStateFlow(false)
    val isLoadingCreate: StateFlow<Boolean> = _isLoadingCreate

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _salesStallsId = MutableStateFlow("")
    val salesStallsId : StateFlow<String> = _salesStallsId

    private val _scheduleTianguisId = MutableStateFlow("")
    val scheduleTianguisId : StateFlow<String> = _scheduleTianguisId

    private val _latitude = MutableStateFlow(19.4326) // Coordenada inicial (CDMX)
    val latitude: StateFlow<Double> = _latitude

    private val _longitude = MutableStateFlow(-99.1332) // Coordenada inicial (CDMX)
    val longitude: StateFlow<Double> = _longitude

    private val _tianguisId = MutableStateFlow("")
    val tianguisId : StateFlow<String> = _tianguisId

    private val _tianguisList = MutableStateFlow<List<TianguisModel>>(emptyList())
    val tianguisList: StateFlow<List<TianguisModel>> = _tianguisList

    private val _scheduleTianguisList = MutableStateFlow<List<ScheduleTianguisModel>>(emptyList())
    val scheduleTianguisList: StateFlow<List<ScheduleTianguisModel>> = _scheduleTianguisList

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

    fun updateCoordinates(lat: Double, lng: Double) {
        _latitude.value = lat
        _longitude.value = lng
    }

    fun onSalesStallsIdChange(value: String) {
        _salesStallsId.value = value
    }

    fun onScheduleTianguisIdChange(value: String) {
        _scheduleTianguisId.value = value
    }

    fun onTianguisIdChange(value: String) {
        _tianguisId.value = value
        getScheduleTianguisByTianguisId(value)
    }

    fun createLocationSalesStalls(){
        val locationSalesStalls = LocationSalesStallsCreateEditModel(
            salesStallsId = _salesStallsId.value,
            scheduleTianguisId = _scheduleTianguisId.value,
            markerMap = MarkerMap(
                type = "Point",
                coordinates = listOf(_longitude.value, _latitude.value) // Coordenadas seleccionadas
            )
        )

        viewModelScope.launch {
            try {
                _isLoadingCreate.value = true
                apiService.createLocationSalesStalls(locationSalesStalls)
                showToast("Locación del puesto creado con éxito")
                _navigationEvent.send(NavigationEvent.TianguisCreated)
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                showToast(errorMessage)
            } catch (e: Exception) {
                showToast("Error al crear la locación: ${e.message}")
                Log.e("dev-debug", "${e.message}")
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
                    showToast("No se encontraron los tianguis")
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

    private fun getScheduleTianguisByTianguisId(scheduleTianguisId: String?) {
        viewModelScope.launch {
            try {
                val response = scheduleTianguisId?.let {
                    scheduleTianguisApiService.getScheduleTianguisByTianguisId(
                        it
                    )
                }
                if (response != null) {
                    if (response.success) {
                        Log.i("dev-debug", "Lista obtenida con éxito")
                        _scheduleTianguisList.value = response.data!!
                    } else {
                        showToast("No se encontraron los horarios")
                    }
                }
                _isLoading.value = false
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ah ocurrido un error")
                showToast("Ocurrio un error al obtener los horarios")
            }
        }
    }
}