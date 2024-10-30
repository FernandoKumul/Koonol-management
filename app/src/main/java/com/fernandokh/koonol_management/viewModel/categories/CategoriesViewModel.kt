package com.fernandokh.koonol_management.viewModel.categories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.CategoriesApiService
import com.fernandokh.koonol_management.data.models.CategoryModel
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.data.pagingSource.CategoryPagingSource
import com.fernandokh.koonol_management.data.pagingSource.UserPagingSource
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CategoriesViewModel : ViewModel() {
    val optionsSort = listOf(
        SelectOption("Más nuevos", "newest"),
        SelectOption("Más viejos", "oldest"),
        SelectOption("A-Z", "a-z"),
        SelectOption("Z-A", "z-a"),
    )

    private val apiService = RetrofitInstance.create(CategoriesApiService::class.java)

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isValueSearch = MutableStateFlow("")
    val isValueSearch: StateFlow<String> = _isValueSearch

    private val _isTotalRecords = MutableStateFlow(0)
    val isTotalRecords: StateFlow<Int> = _isTotalRecords

    private val _isCategoryToDelete = MutableStateFlow<CategoryModel?>(null)
    val isCategoryToDelete: StateFlow<CategoryModel?> = _isCategoryToDelete

    private val _isLoadingDelete = MutableStateFlow(false)
    val isLoadingDelete: StateFlow<Boolean> = _isLoadingDelete


    //Controla la páginación
    private val _userPagingFlow = MutableStateFlow<PagingData<CategoryModel>>(PagingData.empty())
    val userPagingFlow: StateFlow<PagingData<CategoryModel>> = _userPagingFlow

    private val _isSortOption = MutableStateFlow(optionsSort[0])
    val isSortOption: StateFlow<SelectOption> = _isSortOption


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
        searchCategories()
    }

    fun onCategorySelectedForDelete(user: CategoryModel) {
        _isCategoryToDelete.value = user
    }

    fun dismissDialog() {
        _isCategoryToDelete.value = null
    }

    fun deleteCategory() {
        viewModelScope.launch {
            try {
                _isLoadingDelete.value = true
                val idCategory = _isCategoryToDelete.value?.id ?: run {
                    showToast("ID de categoría inválido")
                    return@launch
                }

                apiService.deleteCategoryById(idCategory)
                searchCategories()
                Log.i("dev-debug", "Categoría borrado con el id: $idCategory")
                showToast("Categoría borrado con éxito")
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


    fun searchCategories() {
        _isLoadingDelete.value = true
        viewModelScope.launch {
            val pager =
                Pager(PagingConfig(pageSize = 20, prefetchDistance = 3, initialLoadSize = 20)) {
                    CategoryPagingSource(
                        apiService,
                        _isValueSearch.value,
                        _isSortOption.value.value,
                    ) {
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