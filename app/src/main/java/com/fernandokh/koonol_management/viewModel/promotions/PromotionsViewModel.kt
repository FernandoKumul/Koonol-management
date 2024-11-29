package com.fernandokh.koonol_management.viewModel.promotions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.PromotionApiService
import com.fernandokh.koonol_management.data.models.PromotionSearchModel
import com.fernandokh.koonol_management.data.pagingSource.PromotionPagingSource
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PromotionsViewModelFactory(private val tokenManager: TokenManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PromotionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PromotionsViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PromotionsViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val apiService = RetrofitInstance.create(PromotionApiService::class.java)

    private val _accessToken = MutableStateFlow("")
    val accessToken: StateFlow<String> = _accessToken

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isPromotionToDelete = MutableStateFlow<PromotionSearchModel?>(null)
    val isPromotionToDelete: StateFlow<PromotionSearchModel?> = _isPromotionToDelete

    private val _isLoadingDelete = MutableStateFlow(false)
    val isLoadingDelete: StateFlow<Boolean> = _isLoadingDelete

    //Pagination
    private val _promotionPagingFlow =
        MutableStateFlow<PagingData<PromotionSearchModel>>(PagingData.empty())
    val promotionPagingFlow: StateFlow<PagingData<PromotionSearchModel>> = _promotionPagingFlow

    private val _isValueSearch = MutableStateFlow("")
    val isValueSearch: StateFlow<String> = _isValueSearch

    private val _isTotalRecords = MutableStateFlow(0)
    val isTotalRecords: StateFlow<Int> = _isTotalRecords

    val optionsSort = listOf(
        SelectOption("Más nuevos", "newest"),
        SelectOption("Más viejos", "oldest"),
        SelectOption("A-Z", "a-z"),
        SelectOption("Z-A", "z-a"),
    )

    private val _isSortOption = MutableStateFlow(optionsSort[0])
    val isSortOption: StateFlow<SelectOption> = _isSortOption

    private val _isMaxPay = MutableStateFlow<Double?>(null)
    val isMaxPay: StateFlow<Double?> = _isMaxPay

    private val _isMinPay = MutableStateFlow<Double?>(null)
    val isMinPay: StateFlow<Double?> = _isMinPay

    private val _isStartDate = MutableStateFlow<String?>(null)
    val isStartDate: StateFlow<String?> = _isStartDate

    private val _isEndDate = MutableStateFlow<String?>(null)
    val isEndDate: StateFlow<String?> = _isEndDate


    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun onPromotionSelectedForDelete(promotion: PromotionSearchModel) {
        _isPromotionToDelete.value = promotion
    }

    fun dismissDialog() {
        _isPromotionToDelete.value = null
    }

    fun changeValueSearch(newValue: String) {
        _isValueSearch.value = newValue
    }

    fun changeFilters(sort: SelectOption, minPrice: String, maxPrice: String, startDate: String?, endDate: String?) {
        _isSortOption.value = sort
        _isStartDate.value = startDate
        _isEndDate.value = endDate
        if (maxPrice.isBlank()) {
            _isMaxPay.value = null
        } else {
            _isMaxPay.value = maxPrice.toDouble()
        }

        if (minPrice.isBlank()) {
            _isMinPay.value = null
        } else {
            _isMinPay.value = minPrice.toDouble()
        }
        searchPromotions()
    }

    init {
        viewModelScope.launch {
            val savedToken = tokenManager.accessToken.first()
            _accessToken.value = savedToken
        }
    }


    fun deletePromotion() {
        viewModelScope.launch {
            try {
                _isLoadingDelete.value = true
                val promotionId = _isPromotionToDelete.value?.id ?: run {
                    showToast("ID de categoría inválido")
                    return@launch
                }

                apiService.deleteById("Bearer ${_accessToken.value}", promotionId)
                searchPromotions()
                Log.i("dev-debug", "Promoción borrado con el id: $promotionId")
                showToast("Promoción borrado con éxito")
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error api: $errorMessage")
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrió un error al borrar")
            } finally {
                _isLoadingDelete.value = false
                dismissDialog()
            }
        }
    }


    fun searchPromotions(token: String = _accessToken.value) {
        _isLoading.value = true
        viewModelScope.launch {
            val pager =
                Pager(PagingConfig(pageSize = 20, prefetchDistance = 3, initialLoadSize = 20)) {
                    PromotionPagingSource(
                        apiService,
                        token,
                        _isValueSearch.value,
                        _isSortOption.value.value,
                        null,
                        _isMinPay.value,
                        _isMaxPay.value,
                        _isStartDate.value,
                        _isEndDate.value
                    ) {
                        _isTotalRecords.value = it
                    }
                }.flow.cachedIn(viewModelScope)

            pager.collect { pagingData ->
                _promotionPagingFlow.value = pagingData
                _isLoading.value = false
            }
        }
    }
    fun validateStringToDouble(input: String): Boolean {
        if (input.isBlank()) return true

        return try {
            input.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

}