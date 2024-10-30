package com.fernandokh.koonol_management.ui.screen.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack

@Composable
fun CreateCategoryScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopBarGoBack("Crear Categoría", navController) },
        content = { innerPadding ->
            Column (modifier = Modifier.padding(innerPadding)) {
                Text("Aqui va el contenido")
            }
        },
    )
}