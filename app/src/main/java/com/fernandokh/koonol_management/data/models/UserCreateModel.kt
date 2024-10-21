package com.fernandokh.koonol_management.data.models

data class UserCreateModel(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
    val rolId: String,
    val photo: String?,
    val birthday: String,
    val gender: String,
    val phoneNumber: String,
)
