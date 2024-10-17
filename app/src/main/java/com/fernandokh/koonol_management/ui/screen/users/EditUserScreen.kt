package com.fernandokh.koonol_management.ui.screen.users

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack

@Composable
fun EditUserScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopBarGoBack("Editar Usuario", navController) },
        content = { innerPadding ->
            Column (modifier = Modifier.padding(innerPadding)) {
                Text("Aqui va el contenido")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun PrevEditUserScreen() {
    val navController = rememberNavController()
    EditUserScreen(navController)
}