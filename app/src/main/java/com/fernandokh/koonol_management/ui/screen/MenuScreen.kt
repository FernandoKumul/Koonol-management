package com.fernandokh.koonol_management.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.viewModel.AuthViewModel
import com.fernandokh.koonol_management.viewModel.AuthViewModelFactory

@Composable
fun MenuScreen(navController: NavHostController, tokenManager: TokenManager) {
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(tokenManager)
    )
    val accessToken by authViewModel.accessToken.collectAsState()
    Column {
        Text("Menú")

        Button(onClick = { navController.navigate(Screen.Login.route) }) {
            Text("Cerrar sesión")
        }
        Button(onClick = { navController.navigate(Screen.Users.route) }) {
            Text("Usuarios")
        }
        Text("Token: $accessToken")
    }
}