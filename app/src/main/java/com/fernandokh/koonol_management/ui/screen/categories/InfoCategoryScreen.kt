package com.fernandokh.koonol_management.ui.screen.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme

@Composable
fun InfoCategoryScreen(navController: NavHostController, categoryId: String?) {
    Scaffold(
        topBar = { TopBarGoBack("Categoría", navController) },
        content = { innerPadding ->
            Column (modifier = Modifier.padding(innerPadding)) {
                Text("Aqui va el contenido")
            }
        },
    )
}

@Preview
@Composable
private fun PrevInfoCategoryScreen() {
    KoonolmanagementTheme(dynamicColor = false) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        InfoCategoryScreen(navController, "2")
    }
}