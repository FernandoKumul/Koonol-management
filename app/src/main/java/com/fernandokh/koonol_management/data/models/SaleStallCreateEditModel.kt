package com.fernandokh.koonol_management.data.models

data class SaleStallCreateEditModel(
    val sellerId: String,
    val subCategoryId: String,
    val name: String,
    val photos: ArrayList<String>,
    val description: String,
    val type: Boolean,
    val probation: Boolean,
    val active: Boolean,
)