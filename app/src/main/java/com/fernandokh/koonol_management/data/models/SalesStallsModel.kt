package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class SalesStallsModel(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val photo: String?,
    val description: String,
    val type: String,
    val probation: Boolean,
    val active: Boolean,
    val creationDate: String,
    val updateDate: String,
)