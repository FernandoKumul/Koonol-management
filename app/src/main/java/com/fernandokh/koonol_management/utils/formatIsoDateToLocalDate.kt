package com.fernandokh.koonol_management.utils

import java.text.SimpleDateFormat
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