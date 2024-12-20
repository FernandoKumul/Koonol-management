package com.fernandokh.koonol_management.viewModel.salesstalls

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.SalesStallsApiService
import com.fernandokh.koonol_management.data.models.SalesStallsModel
import com.fernandokh.koonol_management.data.pagingSource.SalesStallsPagingSource
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SalesStallsViewModel : ViewModel() {
    val optionsSort = listOf(
        SelectOption("Más nuevos", "newest"),
        SelectOption("Más viejos", "oldest"),
        SelectOption("A-Z", "a-z"),
        SelectOption("Z-A", "z-a"),
    )

    private val apiService = RetrofitInstance.create(SalesStallsApiService::class.java)

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isValueSearch = MutableStateFlow("")
    val isValueSearch: StateFlow<String> = _isValueSearch

    private val _isTotalRecords = MutableStateFlow(0)
    val isTotalRecords: StateFlow<Int> = _isTotalRecords

    private val _isSalesStallsToDelete = MutableStateFlow<SalesStallsModel?>(null)
    val isSalesStallsToDelete: StateFlow<SalesStallsModel?> = _isSalesStallsToDelete

    private val _isLoadingDelete = MutableStateFlow(false)
    val isLoadingDelete: StateFlow<Boolean> = _isLoadingDelete

    private val _isSortOption = MutableStateFlow(optionsSort[0])
    val isSortOption: StateFlow<SelectOption> = _isSortOption

    private val _salesStallsPagingFlow =
        MutableStateFlow<PagingData<SalesStallsModel>>(PagingData.empty())
    val salesStallsPagingFlow: StateFlow<PagingData<SalesStallsModel>> = _salesStallsPagingFlow

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun changeValueSearch(newValue: String) {
        _isValueSearch.value = newValue
    }

    fun changeFilters(sort: SelectOption) {
        _isSortOption.value = sort
        searchSalesStalls()
    }

    fun onSalesStallsSelectedForDelete(salesStalls: SalesStallsModel) {
        _isSalesStallsToDelete.value = salesStalls
    }

    fun dismissDialog() {
        _isSalesStallsToDelete.value = null
    }

    fun deleteSalesStalls() {
        viewModelScope.launch {
            try {
                _isLoadingDelete.value = true
                val idSalesStalls = _isSalesStallsToDelete.value?.id ?: run {
                    showToast("ID de vendedor inválido")
                    return@launch
                }
                Log.i("dev-debug", "Borrando al puesto antes de la api")
                apiService.deleteSalesStallsById(idSalesStalls)
                Log.i("dev-debug", "Borrando al puesto despues de la api")
                searchSalesStalls()
                Log.i("dev-debug", "Puesto borrado con el id: $idSalesStalls")
                showToast("Vendedor borrado con éxito")
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error al borrar el vendedor: $errorMessage")
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ah ocurrido un error")
                showToast("Ocurrio un error al borrar el vendedor")
            } finally {
                _isLoadingDelete.value = false
                dismissDialog()
            }
        }
    }

    fun searchSalesStalls() {
        _isLoadingDelete.value = true
        viewModelScope.launch {
            val pager =
                Pager(PagingConfig(pageSize = 20, prefetchDistance = 3, initialLoadSize = 20)) {
                    SalesStallsPagingSource(
                        apiService,
                        _isValueSearch.value,
                        _isSortOption.value.value
                    ) {
                        _isTotalRecords.value = it
                    }
                }.flow.cachedIn(viewModelScope)

            pager.collect { pagingData ->
                _salesStallsPagingFlow.value = pagingData
                _isLoadingDelete.value = false
            }
        }
    }
}