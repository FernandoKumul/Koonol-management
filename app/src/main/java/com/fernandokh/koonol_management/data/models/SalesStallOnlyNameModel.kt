package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class SalesStallOnlyNameModel(
    @SerializedName("_id")
    val id: String,
    val name: String,
)
