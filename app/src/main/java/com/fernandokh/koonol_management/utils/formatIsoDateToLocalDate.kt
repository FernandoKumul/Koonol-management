package com.fernandokh.koonol_management.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formatIsoDateToLocalDate(isoDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    outputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val date: Date? = inputFormat.parse(isoDate)

    return if (date != null) {
        outputFormat.format(date)
    } else {
        "Fecha inválida"
    }
}


fun formatIsoDateToDate(isoDate: String): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    outputFormat.timeZone = TimeZone.getTimeZone("UTC")

    return try {
        val date = inputFormat.parse(isoDate)
        date?.let { outputFormat.format(it) }
    } catch (e: Exception) {
        Log.e("dev-debug", "Ha ocurrido un error al parsear la fecha ${e.message}")
        null
    }
}

fun formatDateToDayMonthYear(isoDate: String): String {
    return try {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val date = LocalDate.parse(isoDate, inputFormatter)
        date.format(outputFormatter)
    } catch (e: Exception) {
        Log.e("dev-debug", "Ha ocurrido un error al parsear la fecha ${e.message}")
        "Fecha inválida" // Devuelve un mensaje de error o valor predeterminado
    }
}
