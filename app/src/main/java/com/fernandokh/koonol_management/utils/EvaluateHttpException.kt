package com.fernandokh.koonol_management.utils

import android.util.Log
import com.fernandokh.koonol_management.data.ApiResponseError
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException

fun evaluateHttpException(e: HttpException): String {
    val errorResponse = e.response()
    val errorBody = errorResponse?.errorBody()?.string()

    val gson = Gson()
    return try {
        val error = gson.fromJson(errorBody, ApiResponseError::class.java)
        if (error.errorMessages.isNotEmpty()) {
            "${error.message} | ${error.errorMessages[0].message}"
        } else {
            error.message
        }
    } catch (jsonException: JsonSyntaxException) {
        Log.e("dev-debug", "Error al deserializar JSON: ${jsonException.message}")
        e.message ?: "Error inesperado"
    } catch (exception: Exception) {
        Log.e("dev-debug", "Error inesperado: ${exception.message}")
        "Error inesperado"
    } finally {
        Log.e("dev-debug", "Error Body: $errorBody")
    }
}