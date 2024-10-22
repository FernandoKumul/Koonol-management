package com.fernandokh.koonol_management.viewModel.users

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

class InfoUsersViewModel : ViewModel() {
    private val apiService = RetrofitInstance.create(UserApiService::class.java)

    private val _isUser = MutableStateFlow<UserInModel?>(null)
    val isUser: StateFlow<UserInModel?> = _isUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getUser(userId: String?) {

        if (userId == null) {
            _isUser.value = null
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.getUserById(userId)
                _isUser.value = response.data
                Log.i("dev-debug", "Usuario obtenido con éxito: $userId")
            } catch (e: HttpException) {
                val messageError = evaluateHttpException(e)
                Log.e("dev-debug", "Error Api: $messageError")
                _isUser.value = null
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                _isUser.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}