package com.fernandokh.koonol_management.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.components.router.RouteItem

val routes = listOf(
    RouteItem(Icons.Default.Home, Screen.Menu.route, "Perfil"),
    RouteItem(Icons.Default.Home, Screen.Users.route, "Tiaguis"),
    RouteItem(Icons.Default.Home, Screen.Users.route, "Vendedores"),
    RouteItem(Icons.Default.Home, Screen.Users.route, "Puestos"),
    RouteItem(Icons.Default.Home, Screen.Users.route, "Promociones"),
    RouteItem(Icons.Default.Home, Screen.Users.route, "Categorías"),
    RouteItem(Icons.Default.Home, Screen.Users.route, "Usuarios"),
)