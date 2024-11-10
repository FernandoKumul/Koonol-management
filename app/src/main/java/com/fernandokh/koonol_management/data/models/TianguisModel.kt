package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class TianguisModel(
    @SerializedName("_id")
    val id: String,
    val userId: String,
    val name: String,
    val color: String,
    val dayWeek: String?,
    val photo: String?,
    val indications: String,
    val startTime: String,
    val endTime: String?,
    val locality: String?,
    val active: Boolean?,
    val markerMap: MarkerMap,
    val creationDate: String,
    val updateDate: String,
)
