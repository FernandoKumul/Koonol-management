package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class TianguisModel(
    @SerializedName("_id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("dayWeek")
    val dayWeek: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("indications")
    val indications: String,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("endTime")
    val endTime: String?,
    @SerializedName("locality")
    val locality: String?,
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("markerMap")
    val markerMap: MarkerMap,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("updateDate")
    val updateDate: String,
)
