package com.fernandokh.koonol_management.utils

sealed class NavigationEvent {
    data object Navigate : NavigationEvent()
}