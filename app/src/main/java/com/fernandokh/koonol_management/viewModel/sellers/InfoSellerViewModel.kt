package com.fernandokh.koonol_management.viewModel.sellers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.SellerApiService
import com.fernandokh.koonol_management.data.models.SellerModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class InfoSellerViewModel: ViewModel() {
    private val apiService = RetrofitInstance.create(SellerApiService::class.java)

    private val _isSeller = MutableStateFlow<SellerModel?>(null)
    val isSeller: StateFlow<SellerModel?> = _isSeller

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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
                Log.i("dev-debug", "Vendedor obtenido con éxito: $sellerId")
            } catch (e: HttpException) {
                val messageError = evaluateHttpException(e)
                Log.e("dev-debug", "Error al obtener el vendedor: $messageError")
                _isSeller.value = null
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                _isSeller.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}