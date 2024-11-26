package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class SalesStallsModel(
    @SerializedName("_id")
    val id: String,
    val sellerId: SellerModel,
    val subCategoryId: SubCategoryModel,
    val name: String,
    val photos: ArrayList<String>?,
    val description: String,
    val type: Boolean,
    val probation: Boolean,
    val active: Boolean,
    val creationDate: String,
    val updateDate: String,
)