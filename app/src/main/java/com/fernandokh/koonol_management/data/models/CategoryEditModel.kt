package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class EditCategoryModel(
    val name: String,
    val recommendedRate: Double,
    val subcategories: List<EditSubcategoryModel>,
)

data class EditSubcategoryModel(
    @SerializedName("_id")
    val id: String,
    val name: String,
)