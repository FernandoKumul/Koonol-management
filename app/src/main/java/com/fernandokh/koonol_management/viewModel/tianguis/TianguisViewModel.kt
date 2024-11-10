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
        SelectOption("Z-A", "z-a")
    )

    private val apiService = RetrofitInstance.create(TianguisApiService::class.java)

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isValueSearch = MutableStateFlow("")
    val isValueSearch: StateFlow<String> = _isValueSearch

    private val _isTotalRecords = MutableStateFlow(0)
    val isTotalRecords: StateFlow<Int> = _isTotalRecords

    private val _tianguisPagingFlow = MutableStateFlow<PagingData<TianguisModel>>(PagingData.empty())
    val tianguisPagingFlow: StateFlow<PagingData<TianguisModel>> = _tianguisPagingFlow

    private val _isSortOption = MutableStateFlow(optionsSort[0])
    val isSortOption: StateFlow<SelectOption> = _isSortOption

    // Mostrar mensaje de toast
    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    // Cambiar valor de búsqueda
    fun changeValueSearch(newValue: String) {
        _isValueSearch.value = newValue
        Log.d("TianguisViewModel", "Valor de búsqueda cambiado a: $newValue")
    }

    // Cambiar opción de ordenamiento
    fun changeFilters(sort: SelectOption) {
        _isSortOption.value = sort
        searchTianguis()
    }

    // Buscar tianguis usando paginación
    fun searchTianguis() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("TianguisViewModel", "Iniciando búsqueda de tianguis")
                Log.d("TianguisViewModel", "Parámetros de búsqueda: search = '${_isValueSearch.value}', sort = '${_isSortOption.value.value}'")

                val pager = Pager(
                    PagingConfig(pageSize = 20, prefetchDistance = 3, initialLoadSize = 20)
                ) {
                    TianguisPagingSource(
                        apiService,
                        _isValueSearch.value,
                        _isSortOption.value.value
                    ) {
                        Log.d("TianguisViewModel", "Total de registros recibidos: $it")
                        _isTotalRecords.value = it
                    }
                }.flow.cachedIn(viewModelScope)

                Log.d("TianguisViewModel", "Iniciando colección de datos")
                pager.collect { pagingData ->
                    _tianguisPagingFlow.value = pagingData
                    Log.d("TianguisViewModel", "Datos de tianguis obtenidos correctamente")

                    _isLoading.value = false
                }
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("TianguisViewModel", "HttpException: $errorMessage")
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.e("TianguisViewModel", "Exception: ${e.localizedMessage}")
                showToast("Ocurrió un error al buscar los tianguis")
            } finally {
                _isLoading.value = false
                Log.d("TianguisViewModel", "Finalizando búsqueda de tianguis")
            }
        }
    }
}
