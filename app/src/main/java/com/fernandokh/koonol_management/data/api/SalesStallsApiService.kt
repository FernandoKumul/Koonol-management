package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.SaleStallCreateEditModel
import com.fernandokh.koonol_management.data.models.SalesStallOnlyNameModel
import com.fernandokh.koonol_management.data.models.SalesStallsModel
import com.fernandokh.koonol_management.data.models.SearchModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SalesStallsApiService {
    @GET("sales-stalls")
    suspend fun search(
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String,
    ): ApiResponse<SearchModel<SalesStallsModel>>

    @GET("sales-stalls/{id}")
    suspend fun getSalesStallsById(@Path("id") id: String): ApiResponse<SalesStallsModel>

    @POST("sales-stalls")
    suspend fun createSalesStalls(@Body salesStalls: SaleStallCreateEditModel): ApiResponse<String>

    @PUT("sales-stalls/{id}")
    suspend fun updateSalesStalls(@Path("id") id: String, @Body salesStalls: SaleStallCreateEditModel): ApiResponse<String>

    @DELETE("sales-stalls/{id}")
    suspend fun deleteSalesStallsById(@Path("id") id: String): ApiResponse<String>

    @GET("sales-stalls/only-names")
    suspend fun getSalesStallOnlyNames(@Header("Authorization") authHeader: String): ApiResponse<List<SalesStallOnlyNameModel>>
}