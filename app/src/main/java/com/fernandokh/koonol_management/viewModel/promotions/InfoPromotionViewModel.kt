package com.fernandokh.koonol_management.viewModel.promotions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.PromotionApiService
import com.fernandokh.koonol_management.data.models.PromotionAndNameModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class InfoPromotionViewModel: ViewModel() {
    private val apiService = RetrofitInstance.create(PromotionApiService::class.java)

    private val _isPromotion = MutableStateFlow<PromotionAndNameModel?>(null)
    val isPromotion: StateFlow<PromotionAndNameModel?> = _isPromotion

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getPromotion(accessToken: String, promotionId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response =
                    apiService.getById("Bearer $accessToken", promotionId)
                _isPromotion.value = response.data
                Log.i("dev-debug", "Categoría obtenida con éxito")
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
}