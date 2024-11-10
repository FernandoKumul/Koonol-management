package com.fernandokh.koonol_management.data

data class ApiResponseList<T>(
    val success: Boolean,
    val statusCode: Int,
    val message: String,
    val data: List<T>?,
    val errors: List<ErrorMessages>?
)

data class ErrorMessages(
    val message: String
)
