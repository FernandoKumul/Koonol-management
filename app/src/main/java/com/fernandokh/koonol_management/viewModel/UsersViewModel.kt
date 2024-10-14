package com.fernandokh.koonol_management.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.UserApiService
import com.fernandokh.koonol_management.data.models.UserInModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val apiService = RetrofitInstance.create(UserApiService::class.java)
    private val _users = MutableStateFlow<List<UserInModel>>(emptyList())
    val users: StateFlow<List<UserInModel>> = _users

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

     fun searchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.search("", 1, 10, "newest", "all")
                val usersResponse = response.data?.results ?: emptyList()
                _users.value = usersResponse
                Log.i("dev-debug", "Usuarios obtenidos")
            } catch (e: Exception) {
                Log.i("error-api", e.message ?: "Ah ocurrido un error")
            } finally {
                _isLoading.value = false
            }
        }
    }
}