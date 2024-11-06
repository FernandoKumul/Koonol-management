package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.AuthModel
import com.fernandokh.koonol_management.data.models.TokenModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: AuthModel
    ): ApiResponse<TokenModel>

    @GET("auth/validate")
    suspend fun validateUser(@Header("Authorization") authHeader: String): ApiResponse<Boolean>
}