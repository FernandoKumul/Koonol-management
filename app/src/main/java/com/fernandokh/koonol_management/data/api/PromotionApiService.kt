package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.PromotionAndNameModel
import com.fernandokh.koonol_management.data.models.PromotionModel
import com.fernandokh.koonol_management.data.models.PromotionOutModel
import com.fernandokh.koonol_management.data.models.PromotionSearchModel
import com.fernandokh.koonol_management.data.models.SearchModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PromotionApiService {
    @GET("promotion")
    suspend fun search(
        @Header("Authorization") authHeader: String,
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String,
        @Query("salesStallId") salesStallId: String?,
        @Query("minPay") minPay: Double?,
        @Query("maxPay") maxPay: Double?,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ): ApiResponse<SearchModel<PromotionSearchModel>>

    @GET("promotion/{id}")
    suspend fun getById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): ApiResponse<PromotionAndNameModel>

    @POST("promotion")
    suspend fun create(
        @Header("Authorization") authHeader: String,
        @Body user: PromotionOutModel
    ): ApiResponse<PromotionModel>

    @PUT("promotion/{id}")
    suspend fun update(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Body user: PromotionOutModel
    ): ApiResponse<PromotionModel>

    @DELETE("promotion/{id}")
    suspend fun deleteById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): ApiResponse<PromotionModel>
}