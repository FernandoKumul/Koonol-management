package com.fernandokh.koonol_management.viewModel.tianguis

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.TianguisApiService
import com.fernandokh.koonol_management.data.models.TianguisModel
import com.fernandokh.koonol_management.data.pagingSource.TianguisPagingSource
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TianguisViewModel : ViewModel() {

    val optionsSort = listOf(
        SelectOption("Más nuevos", "newest"),
        SelectOption("Más viejos", "oldest"),
        SelectOption("A-Z", "a-z"),
        SelectOption("Z-A", "z-a"),
    )

    private val apiService = RetrofitInstance.create(TianguisApiService::class.java)

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _isValueSearch = MutableStateFlow("")
    val isValueSearch: StateFlow<String> get() = _isValueSearch

    private val _isTotalRecords = MutableStateFlow(0)
    val isTotalRecords: StateFlow<Int> = _isTotalRecords

    private val _isTianguisToDelete = MutableStateFlow<TianguisModel?>(null)
    val isTianguisToDelete: StateFlow<TianguisModel?> get() = _isTianguisToDelete

    private val _isLoadingDelete = MutableStateFlow(false)
    val isLoadingDelete: StateFlow<Boolean> get() = _isLoadingDelete

    private val _tianguisPagingFlow = MutableStateFlow<PagingData<TianguisModel>>(PagingData.empty())
    val tianguisPagingFlow: StateFlow<PagingData<TianguisModel>> get() = _tianguisPagingFlow

    private val _isSortOption = MutableStateFlow(optionsSort[0])
    val isSortOption: StateFlow<SelectOption> get() = _isSortOption

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun changeValueSearch(newValue: String) {
        _isValueSearch.value = newValue
    }

    fun onTianguisSelectedForDelete(tianguis: TianguisModel) {
        _isTianguisToDelete.value = tianguis
    }

    fun dismissDialog() {
        _isTianguisToDelete.value = null
    }

    fun deleteTianguis() {
        viewModelScope.launch {
            try {
                _isLoadingDelete.value = true
                val id = _isTianguisToDelete.value?.id ?: return@launch
                apiService.deleteTianguisById(id)
                searchTianguis()
                showToast("Tianguis eliminado con éxito")
            } catch (e: HttpException) {
                showToast(evaluateHttpException(e))
            } catch (e: Exception) {
                showToast("Ocurrió un error al eliminar el tianguis")
            } finally {
                _isLoadingDelete.value = false
                dismissDialog()
            }
        }
    }

    fun searchTianguis() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pager = Pager(PagingConfig(pageSize = 20)) {
                    TianguisPagingSource(
                        apiService,
                        _isValueSearch.value,
                        _isSortOption.value.value
                    ) {
                        _isTotalRecords.value = it
                    }
                }.flow.cachedIn(viewModelScope)

                pager.collect {
                    _tianguisPagingFlow.value = it
                }
            } catch (e: Exception) {
                Log.e("TianguisViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
