package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class PromotionModel(
    @SerializedName("_id")
    val id: String,
    val salesStallId: String,
    val startDate: String,
    val endDate: String,
    val pay: Double,
    val creationDate: String
)
