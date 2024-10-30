package com.fernandokh.koonol_management.data.models

data class SearchModel<T>(
    val count: Int,
    val results: List<T>,
)
