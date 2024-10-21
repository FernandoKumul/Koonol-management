package com.fernandokh.koonol_management.utils

import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.components.router.RouteItem

val routes = listOf(
    RouteItem(R.drawable.ic_user_line, Screen.Profile.route, "Perfil"),
    RouteItem(R.drawable.ic_community_line, Screen.Tianguis.route, "Tianguis"),
    RouteItem(R.drawable.ic_wallet_3_line, Screen.Sellers.route, "Vendedores"),
    RouteItem(R.drawable.ic_store_2_line, Screen.SalesStalls.route, "Puestos"),
    RouteItem(R.drawable.ic_advertisement_line, Screen.Promotions.route, "Promociones"),
    RouteItem(R.drawable.ic_price_tag_3_line, Screen.Categories.route, "Categorías"),
    RouteItem(R.drawable.ic_group_line, Screen.Users.route, "Usuarios"),
)