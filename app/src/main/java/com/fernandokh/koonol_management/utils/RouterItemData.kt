package com.fernandokh.koonol_management.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.components.router.RouteItem

val routes = listOf(
    RouteItem(Icons.Default.Home, Screen.Profile.route, "Perfil"),
    RouteItem(Icons.Default.Home, Screen.Tianguis.route, "Tiaguis"),
    RouteItem(Icons.Default.Home, Screen.Sellers.route, "Vendedores"),
    RouteItem(Icons.Default.Home, Screen.SalesStalls.route, "Puestos"),
    RouteItem(Icons.Default.Home, Screen.Promotions.route, "Promociones"),
    RouteItem(Icons.Default.Home, Screen.Categories.route, "Categorías"),
    RouteItem(Icons.Default.Home, Screen.Users.route, "Usuarios"),
)