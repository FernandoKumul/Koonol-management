package com.fernandokh.koonol_management.data.models

data class ProfileEditModel (
    val name: String,
    val lastName: String,
    val photo: String?,
    val birthday: String,
    val gender: String,
    val phoneNumber: String,
)