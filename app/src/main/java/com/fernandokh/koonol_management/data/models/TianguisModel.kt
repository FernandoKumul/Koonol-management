package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class TianguisModel(
    @SerializedName("_id")
    val id: String,
    val markerMap: MarkerMap,
    val userId: String,
    val name: String,
    val color: String,
    val photo: String,
    val indications: String,
    val locality: String,
    val active: Boolean,
    val schedule: List<ScheduleModel>,
    val creationDate: String,
    val updateDate: String,
)

data class ScheduleModel(
    @SerializedName("_id")
    val id: String,
    val tianguisId: String,
    val dayWeek: String,
    val startTime: String,
    val endTime: String,
    val creationDate: String,
    val updateDate: String,
)
