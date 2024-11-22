package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.CategoryModel
import com.fernandokh.koonol_management.data.models.CategoryWithSubModel
import com.fernandokh.koonol_management.data.models.CreateCategoryModel
import com.fernandokh.koonol_management.data.models.SearchModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CategoriesApiService {
    @GET("category")
    suspend fun search(
        @Header("Authorization") authHeader: String,
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String,
    ): ApiResponse<SearchModel<CategoryModel>>

    @GET("category/{id}")
    suspend fun getCategoryById(@Header("Authorization") authHeader: String, @Path("id") id: String): ApiResponse<CategoryWithSubModel>

    @POST("category")
    suspend fun createCategory(@Header("Authorization") authHeader: String, @Body category: CreateCategoryModel ): ApiResponse<CategoryWithSubModel>

    @DELETE("category/{id}")
    suspend fun deleteCategoryById(@Header("Authorization") authHeader: String, @Path("id") id: String): ApiResponse<CategoryModel>
}