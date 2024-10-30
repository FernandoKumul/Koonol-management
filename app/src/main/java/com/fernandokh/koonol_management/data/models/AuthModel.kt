package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class AuthModel(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)