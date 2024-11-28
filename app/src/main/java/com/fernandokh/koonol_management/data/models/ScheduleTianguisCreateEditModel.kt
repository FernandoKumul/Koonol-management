package com.fernandokh.koonol_management.data.models

data class ScheduleTianguisCreateEditModel (
    val tianguisId: String,
    val dayWeek: String,
    val indications: String,
    val startTime: String,
    val endTime: String,
)