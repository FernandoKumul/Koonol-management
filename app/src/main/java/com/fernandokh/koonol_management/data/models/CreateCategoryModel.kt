package com.fernandokh.koonol_management.data.models

data class CreateCategoryModel(
    val name: String,
    val recommendedRate: Double,
    val subcategories: List<CreateSubcategoryModel>,
)

data class CreateSubcategoryModel(
    val name: String,
)
