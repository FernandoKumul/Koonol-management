package com.fernandokh.koonol_management.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle

@Composable
fun CreateTianguisScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopBarGoBack("Crear Tianguis", navController) },
        content = { innerPadding ->
            Column (modifier = Modifier.padding(innerPadding)) {
                Button({
                    navController.navigate(Screen.Maps.route)
                }) {
                    Text("Agregar Ubicación")
                }
            }
        },
    )
}