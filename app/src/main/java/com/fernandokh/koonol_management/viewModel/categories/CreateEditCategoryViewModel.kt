package com.fernandokh.koonol_management.viewModel.categories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fernandokh.koonol_management.data.RetrofitInstance
import com.fernandokh.koonol_management.data.api.CategoriesApiService
import com.fernandokh.koonol_management.data.models.CategoryWithSubModel
import com.fernandokh.koonol_management.data.models.CreateCategoryModel
import com.fernandokh.koonol_management.data.models.CreateSubcategoryModel
import com.fernandokh.koonol_management.data.models.EditCategoryModel
import com.fernandokh.koonol_management.data.models.EditSubcategoryModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.utils.evaluateHttpException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDateTime

data class SubCategoryItemList(
    val id: String,
    val name: String,
    val categoryId: String,
    val creationDate: String,
    val newItem: Boolean = true,
    val error: String? = null
)

class CreateEditCategoryViewModelFactory(private val tokenManager: TokenManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateEditCategoryViewModel::class.java)) {
            return CreateEditCategoryViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CreateEditCategoryViewModel(private val tokenManager: TokenManager) : ViewModel() {
    data class FormErrors(
        val name: String? = null,
        val recommendedRate: String? = null,
    ) {
        fun allErrors(): List<String?> {
            return listOf(
                name,
                recommendedRate
            )
        }
    }

    data class FormCategory(
        val name: String = "",
        val recommendedRate: String = "0",
    )

    private val _form = MutableStateFlow(FormCategory())
    val form: StateFlow<FormCategory> = _form

    private val _formErrors = MutableStateFlow(FormErrors())
    val formErrors: StateFlow<FormErrors> = _formErrors

    private val _dirtyForm = MutableStateFlow(false)


    private val apiService = RetrofitInstance.create(CategoriesApiService::class.java)

    private val _accessToken = MutableStateFlow("")

    private val _isCategory = MutableStateFlow<CategoryWithSubModel?>(null)
    val isCategory: StateFlow<CategoryWithSubModel?> = _isCategory

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingCreate = MutableStateFlow(false)
    val isLoadingCreate: StateFlow<Boolean> = _isLoadingCreate

    private val _isLoadingEdit = MutableStateFlow(false)
    val isLoadingEdit: StateFlow<Boolean> = _isLoadingEdit

    private val _subcategories = MutableStateFlow<List<SubCategoryItemList>>(emptyList())
    val subcategories: StateFlow<List<SubCategoryItemList>> = _subcategories

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _isShowDialog = MutableStateFlow(false)
    val isShowDialog: StateFlow<Boolean> = _isShowDialog

    init {
        viewModelScope.launch {
            val savedToken = tokenManager.accessToken.first()
            _accessToken.value = savedToken
        }
    }

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun resetToastMessage() {
        _toastMessage.value = null
    }

    fun dismissDialog() {
        _isShowDialog.value = false
    }

    fun showDialog() {
        _isShowDialog.value = true
    }


    fun createCategory() {
        val createSubcategories: List<CreateSubcategoryModel> =
            _subcategories.value.map { CreateSubcategoryModel(name = it.name) }

        val category = CreateCategoryModel(
            name = _form.value.name,
            recommendedRate = _form.value.recommendedRate.toDouble(),
            subcategories = createSubcategories
        )
        Log.i("dev-debug", category.toString())

        viewModelScope.launch {
            try {
                _isLoadingCreate.value = true
                apiService.createCategory(
                    "Bearer ${_accessToken.value}",
                    category
                )
                showToast("Categoría creada con éxito")
                _navigationEvent.send(NavigationEvent.Navigate)
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error api: $errorMessage")
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrio un error al crear la categoría")
            } finally {
                _isLoadingCreate.value = false
                dismissDialog()
            }
        }
    }

    fun updateCategory() {
        val createSubcategories: List<EditSubcategoryModel> =
            _subcategories.value.map {
                EditSubcategoryModel(
                    name = it.name,
                    id = if (it.newItem) "" else it.id
                )
            }

        val category = EditCategoryModel(
            name = _form.value.name,
            recommendedRate = _form.value.recommendedRate.toDouble(),
            subcategories = createSubcategories
        )
        Log.i("dev-debug", category.toString())

        viewModelScope.launch {
            try {
                _isLoadingEdit.value = true
                apiService.updateCategory(
                    "Bearer ${_accessToken.value}",
                    _isCategory.value?.id ?: "",
                    category
                )
                showToast("Categoría actualizada con éxito")
                Log.i("dev-debug", "Antes")
                _navigationEvent.send(NavigationEvent.Navigate)
                Log.i("dev-debug", "Despues")
            } catch (e: HttpException) {
                val errorMessage = evaluateHttpException(e)
                Log.e("dev-debug", "Error api: $errorMessage")
                showToast(errorMessage)
            } catch (e: Exception) {
                Log.i("dev-debug", e.message ?: "Ha ocurrido un error")
                showToast("Ocurrio un error al actualizar la categoría")
            } finally {
                _isLoadingEdit.value = false
                dismissDialog()
            }
        }
    }

    fun getCategory(accessToken: String, categoryId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response =
                    apiService.getCategoryById("Bearer $accessToken", categoryId)
                _isCategory.value = response.data
                if (_isCategory.value != null) {
                    _form.value = FormCategory(
                        name = _isCategory.value!!.name,
                        recommendedRate = _isCategory.value!!.recommendedRate.toString(),
                    )

                    _subcategories.value = _isCategory.value!!.subcategories.map {
                        SubCategoryItemList(
                            id = it.id,
                            name = it.name,
                            creationDate = it.creationDate,
                            categoryId = it.categoryId,
                            newItem = false
                        )
                    }
                }
                Log.i("dev-debug", "Categoría obtenida con éxito")
            } catch (e: HttpException) {
                val messageError = evaluateHttpException(e)
                Log.e("dev-debug", "Error al obtener la categoría: $messageError")
                _isCategory.value = null
            } catch (e: Exception) {
                Log.e("dev-debug", e.message ?: "Ha ocurrido un error")
                _isCategory.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun addSubcategory() {
        val newSubcategory = SubCategoryItemList(
            id = LocalDateTime.now().toString(),
            name = "",
            categoryId = "",
            creationDate = "",
        )

        _subcategories.update { listOf(newSubcategory) + it }
    }

    fun removeSubcategory(id: String) {
        _subcategories.update { sub -> sub.filter { it.id != id } }
    }

    fun onSubCategoryChange(value: String, index: Int) {
        val updatedList = _subcategories.value.toMutableList()
        updatedList[index] = updatedList[index].copy(name = value)
        _subcategories.value = updatedList

        if (_dirtyForm.value) {
            validateNameSubcategory(value, index)
        }
    }

    fun onNameCategoryChange(value: String) {
        _form.value = _form.value.copy(name = value)

        if (_dirtyForm.value) {
            validateNameCategory()
        }
    }

    fun onRecommendedRateChange(value: String) {
        _form.value = _form.value.copy(recommendedRate = value)

        if (_dirtyForm.value) {
            validateRecommendedRate()
        }
    }

    private fun validateNameSubcategory(value: String, index: Int) {
        val updatedList = _subcategories.value.toMutableList()
        if (value.isBlank()) {
            updatedList[index] = updatedList[index].copy(error = "El nombre es requerido")
        } else {
            updatedList[index] = updatedList[index].copy(error = null)
        }
        _subcategories.value = updatedList
    }

    private fun validateNameCategory() {
        val name = _form.value.name
        if (name.isBlank()) {
            _formErrors.value = _formErrors.value.copy(name = "El nombre es requerido")
        } else {
            _formErrors.value = _formErrors.value.copy(name = null)
        }
    }

    private fun validateRecommendedRate() {
        val rate = _form.value.recommendedRate

        try {
            val rateDouble = rate.toDouble()
            if (rateDouble < 0) {
                _formErrors.value =
                    _formErrors.value.copy(recommendedRate = "La tarifa no puede ser negativa")
            } else {
                _formErrors.value = _formErrors.value.copy(recommendedRate = null)
            }
        } catch (e: Exception) {
            _formErrors.value =
                _formErrors.value.copy(recommendedRate = "La tarifa no es número válido")
        }
    }

    fun isFormValid(): Boolean {
        _dirtyForm.value = true
        validateNameCategory()
        validateRecommendedRate()

        var validSubCategories = true
        _subcategories.value.forEachIndexed { index, item ->
            validateNameSubcategory(item.name, index)
        }

        for (item in _subcategories.value) {
            val valid = item.error == null
            if (!valid) {
                validSubCategories = false
                break
            }
        }

        if (!validSubCategories) return false

        return _formErrors.value.allErrors().all { it === null }
    }
}