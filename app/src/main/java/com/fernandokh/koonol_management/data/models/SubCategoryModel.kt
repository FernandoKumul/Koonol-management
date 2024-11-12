package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class SubCategoryModel(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val categoryId: String,
    val creationDate: String
)
