package com.fernandokh.koonol_management.data.models

data class UserSearchModel(
    val count: Int,
    val results: List<UserInModel>,
)
