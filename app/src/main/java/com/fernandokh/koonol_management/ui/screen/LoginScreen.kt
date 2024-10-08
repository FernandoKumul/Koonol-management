package com.fernandokh.koonol_management.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.Screen

@Composable
fun LoginScreen(navController: NavHostController) {
    Column {
        Text("Iniciar sesión")
        Button(onClick = { navController.navigate(Screen.Menu.route) }) {
            Text("Continuar")
        }
    }
}