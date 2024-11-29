package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class PromotionAndNameModel(
    @SerializedName("_id")
    val id: String,
    val salesStallId: SalesStallOnlyNameModel,
    val startDate: String,
    val endDate: String,
    val pay: Double,
    val creationDate: String
)
