package com.fernandokh.koonol_management.viewModel.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.UserApiService
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProfileViewModel : ViewModel() {
    private val apiService = RetrofitInstance.create(UserApiService::class.java)

    private val _isUser = MutableStateFlow<UserInModel?>(null)
    val isUser: StateFlow<UserInModel?> = _isUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getUser() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response =
                    apiService.getProfile("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI2NzE1YjFhN2RjNTAxZTZlZWZjNTJkMTAiLCJ1c2VyTmFtZSI6Ikpvc2UgRmVybmFuZG8iLCJyb2xJZCI6IjY3MDMxODEwNGQ5ODI0YjRkYTBkOWE5YiIsInJvbE5hbWUiOiJBZG1pbmlzdHJhZG9yIiwiaWF0IjoxNzI5OTIzMzY1LCJleHAiOjE3MzAwOTYxNjV9.imxcfScfVzEDIGwguisL4lQ-8OnGKHoE9jxF2RTfZEs")
                _isUser.value = response.data
                Log.i("dev-debug", "Perfil obtenido con éxito")
            } catch (e: HttpException) {
                val messageError = evaluateHttpException(e)
                Log.e("dev-debug", "Error al obtener el perfil: $messageError")
                _isUser.value = null
            } catch (e: Exception) {
                Log.e("dev-debug", e.message ?: "Ha ocurrido un error")
                _isUser.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

}