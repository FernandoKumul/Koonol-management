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

    private val _isValueSearch = MutableStateFlow("")
    val isValueSearch: StateFlow<String> = _isValueSearch

    private val _isTotalRecords = MutableStateFlow(0)
    val isTotalRecords: StateFlow<Int> = _isTotalRecords

    fun changeValueSearch(newValue: String) {
        _isValueSearch.value = newValue
    }

     fun searchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.search(_isValueSearch.value, 1, 10, "newest", "all")
                val data = response.data
                _users.value = data?.results ?: emptyList()
                _isTotalRecords.value = data?.count ?: 0
                Log.i("dev-debug", "Usuarios obtenidos")
            } catch (e: Exception) {
                Log.i("error-api", e.message ?: "Ah ocurrido un error")
            } finally {
                _isLoading.value = false
            }
        }
    }
}