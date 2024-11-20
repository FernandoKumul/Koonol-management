package com.fernandokh.koonol_management.data

data class ApiResponseError(
    val success: Boolean,
    val statusCode: Long,
    val message: String,
    val errorMessages: List<Error>,
)

data class Error(
    val message: String,
)
