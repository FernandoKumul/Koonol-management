package com.fernandokh.koonol_management.viewModel.salesstalls

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.SalesStallsApiService
import com.fernandokh.koonol_management.data.models.SalesStallsModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class InfoSaleStallViewModel : ViewModel() {
    private val apiService = RetrofitInstance.create(SalesStallsApiService::class.java)

    private val _isSaleStall = MutableStateFlow<SalesStallsModel?>(null)
    val isSaleStall: StateFlow<SalesStallsModel?> = _isSaleStall

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getSaleStall(saleStallId: String?) {
        if (saleStallId == null) {
            _isSaleStall.value = null
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.getSalesStallsById(saleStallId)
                _isSaleStall.value = response.data
                Log.i("dev-debug", "Vendedor obtenido con éxito: $saleStallId")
            } catch (e: HttpException) {
                val messageError = evaluateHttpException(e)
                Log.e("dev-debug", "Error al obtener el vendedor: $messageError")
                _isSaleStall.value = null
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                _isSaleStall.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}