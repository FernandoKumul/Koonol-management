package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.CategoryModel
import com.fernandokh.koonol_management.data.models.SearchModel
import com.fernandokh.koonol_management.data.models.SubCategoryModel
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CategoriesApiService {
    @GET("category")
    suspend fun search(
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String,
    ): ApiResponse<SearchModel<CategoryModel>>

    @DELETE("category/{id}")
    suspend fun deleteCategoryById(@Path("id") id: String): ApiResponse<CategoryModel>

    // Subcategorias

    @GET("subcategory")
    suspend fun getAllSubcategories(): ApiResponse<List<SubCategoryModel>>
}