package com.fernandokh.koonol_management.viewModel.sellers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.SellerApiService
import com.fernandokh.koonol_management.data.models.SellerModel
import com.fernandokh.koonol_management.data.pagingSource.SellerPagingSource
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SellersViewModel: ViewModel() {
    val optionsSort = listOf(
        SelectOption("Más nuevos", "newest"),
        SelectOption("Más viejos", "oldest"),
        SelectOption("A-Z", "a-z"),
        SelectOption("Z-A", "z-a"),
    )

    val optionsGender = listOf(
        SelectOption("Todos", ""),
        SelectOption("Masculino", "male"),
        SelectOption("Femenino", "female"),
        SelectOption("Otros", "other"),
    )

    private val apiService = RetrofitInstance.create(SellerApiService::class.java)
    private val _sellers = MutableStateFlow<List<SellerModel>>(emptyList())
    val sellers: StateFlow<List<SellerModel>> = _sellers

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isValueSearch = MutableStateFlow("")
    val isValueSearch: StateFlow<String> = _isValueSearch

    private val _isTotalRecords = MutableStateFlow(0)
    val isTotalRecords: StateFlow<Int> = _isTotalRecords

    private val _isSellerToDelete = MutableStateFlow<SellerModel?>(null)
    val isSellerToDelete: StateFlow<SellerModel?> = _isSellerToDelete

    private val _isLoadingDelete = MutableStateFlow(false)
    val isLoadingDelete: StateFlow<Boolean> = _isLoadingDelete

    private val _isSortOption = MutableStateFlow(optionsSort[0])
    val isSortOption: StateFlow<SelectOption> = _isSortOption

    private val _isGenderFilterOption = MutableStateFlow(optionsGender[0])
    val isGenderFilterOption: StateFlow<SelectOption> = _isGenderFilterOption

    private val _sellerPagingFlow = MutableStateFlow<PagingData<SellerModel>>(PagingData.empty())
    val sellerPagingFlow: StateFlow<PagingData<SellerModel>> = _sellerPagingFlow

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun changeValueSearch(newValue: String) {
        _isValueSearch.value = newValue
    }

    fun changeFilters(sort: SelectOption, gender: SelectOption) {
        _isSortOption.value = sort
        _isGenderFilterOption.value = gender
        searchSellers()
    }

    fun onSellerSelectedForDelete(seller: SellerModel) {
        _isSellerToDelete.value = seller
    }

    fun dismissDialog() {
        _isSellerToDelete.value = null
    }

    fun deleteSeller() {
        viewModelScope.launch {
            try {
                _isLoadingDelete.value = true
                val idSeller = _isSellerToDelete.value?.id ?: run {
                    showToast("ID de vendedor inválido")
                    return@launch
                }

                apiService.getSellerById(idSeller)
                searchSellers()
                Log.i("dev-debug", "Vendedor borrado con el id: $idSeller")
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


    fun searchSellers() {
        _isLoadingDelete.value = true
        viewModelScope.launch {
            val pager =
                Pager(PagingConfig(pageSize = 20, prefetchDistance = 3, initialLoadSize = 20)) {
                    SellerPagingSource(
                        apiService,
                        _isValueSearch.value,
                        _isSortOption.value.value,
                        _isGenderFilterOption.value.value
                    ) {
                        _isTotalRecords.value = it
                    }
                }.flow.cachedIn(viewModelScope)

            pager.collect { pagingData ->
                _sellerPagingFlow.value = pagingData
                _isLoadingDelete.value = false
            }
        }
    }
}