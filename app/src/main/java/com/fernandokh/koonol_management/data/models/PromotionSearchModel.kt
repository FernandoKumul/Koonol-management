package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class PromotionSearchModel (
    @SerializedName("_id")
    val id: String,
    val salesStall: SalesStallOnlyNameModel,
    val startDate: String,
    val endDate: String,
    val pay: Double,
    val creationDate: String
)