package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.UserModel
import com.fernandokh.koonol_management.data.models.UserSearchModel
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {
    @GET("users")
    suspend fun search(
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String,
        @Query("rol") rol: String,
    ): ApiResponse<UserSearchModel>

    @DELETE("users/{id}")
    suspend fun deleteUserById(@Path("id") id: String): ApiResponse<UserModel>
}
