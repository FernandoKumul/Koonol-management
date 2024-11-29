package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class TianguisEditModel(
    val userId: String,
    val name: String,
    val color: String,
    val schedule: ScheduleEditModel,
    val photo: String?,
    val indications: String,
    val locality: String?,
    val active: Boolean,
    val markerMap: MarkerMap
)

data class ScheduleEditModel(
    @SerializedName("_id")
    val id: String,
    val dayWeek: String?,
    val startTime: String,
    val endTime: String?,
)
