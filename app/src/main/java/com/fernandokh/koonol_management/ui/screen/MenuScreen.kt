package com.fernandokh.koonol_management.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.Screen

@Composable
fun MenuScreen(navController: NavHostController) {
    Column {
        Text("Menú")
        Button(onClick = { navController.navigate(Screen.Login.route) }) {
            Text("Cerrar sesión")
        }
        Button(onClick = { navController.navigate(Screen.Users.route) }) {
            Text("Usuarios")
        }
    }
}