package com.fernandokh.koonol_management.data.models

import com.google.gson.annotations.SerializedName

data class RolModel(
    @SerializedName("_id")
    val id: String,
    val name: String,
)