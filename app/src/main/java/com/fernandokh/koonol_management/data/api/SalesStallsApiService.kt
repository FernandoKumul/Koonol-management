package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.SalesStallsModel
import com.fernandokh.koonol_management.data.models.SearchModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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

//    @POST("sales-stalls")
//    suspend fun createSalesStalls(@Body salesStalls: SalesStallsCreateEditModel): ApiResponse<SalesStallsModel>
//
//    @PUT("sales-stalls/{id}")
//    suspend fun updateSalesStalls(@Path("id") id: String, @Body salesStalls: SalesStallsCreateEditModel): ApiResponse<SalesStallsModel>

    @DELETE("sales-stalls/{id}")
    suspend fun deleteSalesStallsById(@Path("id") id: String): ApiResponse<SalesStallsModel>

}