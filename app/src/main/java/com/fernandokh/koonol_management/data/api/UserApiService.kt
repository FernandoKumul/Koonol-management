package com.fernandokh.koonol_management.data.api

import com.fernandokh.koonol_management.data.ApiResponse
import com.fernandokh.koonol_management.data.models.ChangePasswordModel
import com.fernandokh.koonol_management.data.models.ProfileEditModel
import com.fernandokh.koonol_management.data.models.SearchModel
import com.fernandokh.koonol_management.data.models.UserCreateModel
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.data.models.UserModel
import com.fernandokh.koonol_management.data.models.UserUpdateModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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
    ): ApiResponse<SearchModel<UserInModel>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): ApiResponse<UserInModel>

    @POST("users")
    suspend fun createUser(@Body user: UserCreateModel): ApiResponse<UserModel>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: UserUpdateModel): ApiResponse<UserModel>

    @DELETE("users/{id}")
    suspend fun deleteUserById(@Path("id") id: String): ApiResponse<UserModel>

    @GET("users/profile")
    suspend fun getProfile(@Header("Authorization") authHeader: String): ApiResponse<UserInModel>

    @PUT("users/profile/")
    suspend fun updateProfile(@Header("Authorization") authHeader: String, @Body profile: ProfileEditModel): ApiResponse<UserModel>

    @PUT("users/profile/change-password")
    suspend fun changePasswordProfile(@Header("Authorization") authHeader: String, @Body password: ChangePasswordModel): ApiResponse<Boolean>
}
