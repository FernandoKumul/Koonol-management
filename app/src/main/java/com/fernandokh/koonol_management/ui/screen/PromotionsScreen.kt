package com.fernandokh.koonol_management.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle

@Composable
fun PromotionsScreen(navController: NavHostController, drawerState: DrawerState) {
    Scaffold(
        topBar = { TopBarMenuTitle("Promociones", drawerState) },
        content = { innerPadding ->
            Column (modifier = Modifier.padding(innerPadding)) {
                Text("Aqui va el contenido")
            }
        },
    )
}