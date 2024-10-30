package com.fernandokh.koonol_management.viewModel.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.UserApiService
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProfileViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProfileViewModel(private val tokenManager: TokenManager) : ViewModel() {
    private val apiService = RetrofitInstance.create(UserApiService::class.java)

//    private val _accessToken = MutableStateFlow("")
//    val accessToken: StateFlow<String> = _accessToken

    private val _isUser = MutableStateFlow<UserInModel?>(null)
    val isUser: StateFlow<UserInModel?> = _isUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

//    init {
//        viewModelScope.launch {
//            tokenManager.accessToken.collectLatest { savedToken ->
//                _accessToken.value = savedToken
//            }
//        }
//    }

    fun getUser(accessToken: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.i("dev-debug", "Token: $accessToken")
                val response =
                    apiService.getProfile("Bearer $accessToken")
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