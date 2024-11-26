package com.fernandokh.koonol_management.viewModel.tianguis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.TianguisApiService
import com.fernandokh.koonol_management.data.models.TianguisModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class InfoTianguisViewModel : ViewModel() {
    private val apiService = RetrofitInstance.create(TianguisApiService::class.java)

    private val _isTianguis = MutableStateFlow<TianguisModel?>(null)
    val isTianguis: StateFlow<TianguisModel?> = _isTianguis

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getTianguis(tianguisId: String?) {
        if (tianguisId == null) {
            _isTianguis.value = null
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.getTianguisById(tianguisId)
                _isTianguis.value = response.data
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                _isTianguis.value = null
            } catch (e: Exception) {
                _isTianguis.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
