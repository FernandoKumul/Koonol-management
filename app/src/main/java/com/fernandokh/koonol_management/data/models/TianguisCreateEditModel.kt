package com.fernandokh.koonol_management.data.models

data class TianguisCreateEditModel(
    val userId: String,
    val name: String,
    val color: String,
    val photo: String?,
    val indications: String,
    val locality: String?,
    val active: Boolean,
    val markerMap: MarkerMap
)
