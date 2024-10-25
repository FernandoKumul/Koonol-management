package com.fernandokh.koonol_management.data.models

data class SellerCreateEditModel(
    val name: String,
    val lastName: String,
    val email: String?,
    val photo: String?,
    val birthday: String,
    val gender: String,
    val phoneNumber: String?,
)
