package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class CategoryWithSubModel(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val recommendedRate: Double,
    val creationDate: String,
    val subcategories: List<Subcategory>,
)

data class Subcategory(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val categoryId: String,
    val creationDate: String,
)
