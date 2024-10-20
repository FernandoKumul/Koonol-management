package com.fernandokh.koonol_management.viewModel.users

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateUserViewModel: ViewModel() {
    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    private val _isLoadingCreate = MutableStateFlow(false)
    val isLoadingCreate: StateFlow<Boolean> = _isLoadingCreate


    fun dismissDialog() {
        _isShowDialog.value = false
    }

    fun showDialog() {
        _isShowDialog.value = true
    }
    fun createUser() {

    }
}