package com.fernandokh.koonol_management.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fernandokh.koonol_management.data.ApiResponseError
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.UserApiService
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.data.pagingSource.UserPagingSource
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UserViewModel : ViewModel() {

    private val apiService = RetrofitInstance.create(UserApiService::class.java)
    private val _users = MutableStateFlow<List<UserInModel>>(emptyList())
    val users: StateFlow<List<UserInModel>> = _users

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isValueSearch = MutableStateFlow("")
    val isValueSearch: StateFlow<String> = _isValueSearch

    private val _isTotalRecords = MutableStateFlow(0)
    val isTotalRecords: StateFlow<Int> = _isTotalRecords

    private val _isUserToDelete = MutableStateFlow<UserInModel?>(null)
    val isUserToDelete: StateFlow<UserInModel?> = _isUserToDelete

    private val _isLoadingDelete = MutableStateFlow(false)
    val isLoadingDelete:StateFlow<Boolean> = _isLoadingDelete

    //Controla la páginación
    private val _userPagingFlow = MutableStateFlow<PagingData<UserInModel>>(PagingData.empty())
    val userPagingFlow:StateFlow<PagingData<UserInModel>> = _userPagingFlow


    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun changeValueSearch(newValue: String) {
        _isValueSearch.value = newValue
    }

    fun onUserSelectedForDelete(user: UserInModel) {
        _isUserToDelete.value = user
    }

    fun dismissDialog() {
        _isUserToDelete.value = null
    }

    fun deleteUser() {
        viewModelScope.launch {
            try {
                _isLoadingDelete.value = true
                val idUser = _isUserToDelete.value?.id ?: run {
                    showToast("ID de usuario inválido")
                    return@launch
                }

                apiService.deleteUserById(idUser)
                searchUsers()
                Log.i("dev-debug", "Usuario borrado con el id: $idUser")
                showToast("Usuario borrado con éxito")
            } catch (e: HttpException) {
                val errorResponse = e.response()
                val errorBody = errorResponse?.errorBody()?.string()

                val gson = Gson()
                val error = gson.fromJson(errorBody, ApiResponseError::class.java)

                Log.e("dev-debug", "Error Body: $errorBody")
                showToast(error.message)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ah ocurrido un error")
                showToast("Ocurrio un error al borrar")
            } finally {
                _isLoadingDelete.value = false
                dismissDialog()
            }
        }
    }


    fun searchUsers() {
        _isLoadingDelete.value = true
        viewModelScope.launch {
            val pager = Pager(PagingConfig(pageSize = 20, prefetchDistance = 3, initialLoadSize = 20)) {
                UserPagingSource(apiService, _isValueSearch.value, "newest", "all") {
                    _isTotalRecords.value = it
                }
            }.flow.cachedIn(viewModelScope)

            pager.collect { pagingData ->
                _userPagingFlow.value = pagingData
                _isLoadingDelete.value = false
            }
        }
    }
}