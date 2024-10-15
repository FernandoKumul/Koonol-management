package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
    val rol: String,
    val photo: String?,
    val birthday: String,
    val gender: String,
    val phoneNumber: String,
    val creationDate: String,
    val updateDate: String,
)
