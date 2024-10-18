package com.fernandokh.koonol_management.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface MenuItem {

    data class Option(
        val name: String,
        val icon: ImageVector?,
        val color: Color,
        val enabled: Boolean = true
    ) : MenuItem

    object Divider : MenuItem
}