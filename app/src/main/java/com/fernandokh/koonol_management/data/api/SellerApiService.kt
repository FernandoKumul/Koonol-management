package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.SearchModel
import com.fernandokh.koonol_management.data.models.SellerCreateEditModel
import com.fernandokh.koonol_management.data.models.SellerModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @GET("sellers/all")
    suspend fun getAllSellers(): ApiResponse<List<SellerModel>>

    @GET("sellers/{id}")
    suspend fun getSellerById(@Path("id") id: String): ApiResponse<SellerModel>

    @POST("sellers")
    suspend fun createSeller(@Body seller: SellerCreateEditModel): ApiResponse<SellerModel>

    @PUT("sellers/{id}")
    suspend fun updateSeller(@Path("id") id: String, @Body seller: SellerCreateEditModel): ApiResponse<SellerModel>

    @DELETE("sellers/{id}")
    suspend fun deleteSellerById(@Path("id") id: String): ApiResponse<SellerModel>
}