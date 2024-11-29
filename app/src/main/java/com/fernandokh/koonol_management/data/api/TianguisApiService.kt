package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.ApiResponseList
import com.fernandokh.koonol_management.data.models.SearchModel
import com.fernandokh.koonol_management.data.models.TianguisCreateEditModel
import com.fernandokh.koonol_management.data.models.TianguisModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TianguisApiService {

    @GET("tianguis")
    suspend fun search(
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String
    ): ApiResponse<SearchModel<TianguisModel>>

    @GET("tianguis/{id}")
    suspend fun getTianguisById(@Path("id") id: String): ApiResponse<TianguisModel>

    @POST("tianguis")
    suspend fun createTianguis(@Body tianguis: TianguisCreateEditModel): ApiResponse<TianguisModel>

    @PUT("tianguis/{id}")
    suspend fun updateTianguis(
        @Path("id") id: String,
        @Body tianguis: TianguisCreateEditModel
    ): ApiResponse<TianguisModel>

    @DELETE("tianguis/{id}")
    suspend fun deleteTianguisById(@Path("id") id: String): ApiResponse<TianguisModel>

    @GET("tianguis/all")
    suspend fun getAllTianguis(): ApiResponseList<TianguisModel>
}
