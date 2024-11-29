package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.ScheduleTianguisCreateEditModel
import com.fernandokh.koonol_management.data.models.ScheduleTianguisModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ScheduleTianguisApiService {
    @GET("schedule-tianguis")
    suspend fun getScheduleTianguis(): ApiResponse<List<ScheduleTianguisModel>>

    @POST("schedule-tianguis")
    suspend fun createScheduleTianguis(@Body scheduleTianguis: ScheduleTianguisCreateEditModel): ApiResponse<ScheduleTianguisCreateEditModel>
}