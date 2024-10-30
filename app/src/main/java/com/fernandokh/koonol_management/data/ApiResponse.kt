package com.fernandokh.koonol_management.data

data class ApiResponse<T>(
    val success: Boolean,
    val statusCode: Int,
    val message: String,
    val data: T?,
    val errors: List<ErrorMessage>?
)

data class ErrorMessage(
    val message: String
)
