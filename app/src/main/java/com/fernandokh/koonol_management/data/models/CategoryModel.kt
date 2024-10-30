package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class CategoryModel(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val recommendedRate: Double,
    val creationDate: String
)
