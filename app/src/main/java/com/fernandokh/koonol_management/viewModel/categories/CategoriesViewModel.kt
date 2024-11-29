package com.fernandokh.koonol_management.viewModel.categories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.CategoriesApiService
import com.fernandokh.koonol_management.data.models.CategoryModel
import com.fernandokh.koonol_management.data.models.SellerModel
import com.fernandokh.koonol_management.data.models.SubCategoryModel
import com.fernandokh.koonol_management.data.pagingSource.CategoryPagingSource
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CategoriesViewModelFactory(private val tokenManager: TokenManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            return CategoriesViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CategoriesViewModel(private val tokenManager: TokenManager) : ViewModel() {
    val optionsSort = listOf(
        SelectOption("Más nuevos", "newest"),
        SelectOption("Más viejos", "oldest"),
        SelectOption("A-Z", "a-z"),
        SelectOption("Z-A", "z-a"),
    )

    private val apiService = RetrofitInstance.create(CategoriesApiService::class.java)

    private val _accessToken = MutableStateFlow("")
    val accessToken: StateFlow<String> = _accessToken

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

    private val _subCategoriesList = MutableStateFlow<List<SubCategoryModel>>(emptyList())
    val subCategoriesList: StateFlow<List<SubCategoryModel>> = _subCategoriesList

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

    init {
        viewModelScope.launch {
            val savedToken = tokenManager.accessToken.first()
            _accessToken.value = savedToken
        }
    }

    fun deleteCategory() {
        viewModelScope.launch {
            try {
                _isLoadingDelete.value = true
                val idCategory = _isCategoryToDelete.value?.id ?: run {
                    showToast("ID de categoría inválido")
                    return@launch
                }

                apiService.deleteCategoryById("Bearer ${_accessToken.value}", idCategory)
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


    fun searchCategories(token: String = _accessToken.value) {
        _isLoadingDelete.value = true
        viewModelScope.launch {
            val pager =
                Pager(PagingConfig(pageSize = 20, prefetchDistance = 3, initialLoadSize = 20)) {
                    CategoryPagingSource(
                        apiService,
                        token,
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

    fun getAllSubcategories() {
        viewModelScope.launch {
            try {
                val response = apiService.getAllSubcategories()
                if (response.success) {
                    Log.i("dev-debug", "Lista obtenida con éxito")
                    _subCategoriesList.value = response.data!!
                } else {
                    showToast("No se encontraron subcategorías")
                }
                _isLoading.value = false
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ah ocurrido un error")
                showToast("Ocurrio un error al obtener las subcategorías")
            }
        }
    }
}