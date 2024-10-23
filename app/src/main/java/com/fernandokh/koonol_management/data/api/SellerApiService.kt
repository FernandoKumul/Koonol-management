package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.SearchModel
import com.fernandokh.koonol_management.data.models.SellerModel
import retrofit2.http.GET
import retrofit2.http.Query

interface SellerApiService {
    @GET("sellers")
    suspend fun search(
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String,
        @Query("gender") gender: String,
    ): ApiResponse<SearchModel<SellerModel>>
}