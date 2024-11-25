package com.fernandokh.koonol_management.viewModel.categories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.CategoriesApiService
import com.fernandokh.koonol_management.data.models.CategoryWithSubModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class InfoCategoryViewModel: ViewModel() {
    private val apiService = RetrofitInstance.create(CategoriesApiService::class.java)

    private val _isCategory = MutableStateFlow<CategoryWithSubModel?>(null)
    val isCategory: StateFlow<CategoryWithSubModel?> = _isCategory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getUser(accessToken: String, categoryId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response =
                    apiService.getCategoryById("Bearer $accessToken", categoryId)
                _isCategory.value = response.data
                Log.i("dev-debug", "Categoría obtenida con éxito")
            } catch (e: HttpException) {
                val messageError = evaluateHttpException(e)
                Log.e("dev-debug", "Error al obtener la categoría: $messageError")
                _isCategory.value = null
            } catch (e: Exception) {
                Log.e("dev-debug", e.message ?: "Ha ocurrido un error")
                _isCategory.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

}