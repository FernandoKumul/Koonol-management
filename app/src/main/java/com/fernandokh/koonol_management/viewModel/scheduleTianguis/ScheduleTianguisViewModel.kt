package com.fernandokh.koonol_management.viewModel.scheduleTianguis

import androidx.lifecycle.ViewModel
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.ScheduleTianguisApiService
import com.fernandokh.koonol_management.data.models.ScheduleTianguisModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScheduleTianguisViewModel : ViewModel() {
    private val apiService = RetrofitInstance.create(ScheduleTianguisApiService::class.java)

    private val _isScheduleTianguis = MutableStateFlow<ScheduleTianguisModel?>(null)
    val isScheduleTianguis: StateFlow<ScheduleTianguisModel?> = _isScheduleTianguis
}