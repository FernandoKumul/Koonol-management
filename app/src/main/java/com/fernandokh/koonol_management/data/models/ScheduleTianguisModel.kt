package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class ScheduleTianguisModel(
    @SerializedName("_id")
    val id: String,
    val tianguisId: String,
    val dayWeek: String,
    val indications: String,
    val startTime: String,
    val endTime: String,
    val creationDate: String,
    val updateDate: String
)