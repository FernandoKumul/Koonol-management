package com.fernandokh.koonol_management.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle
import com.fernandokh.koonol_management.ui.components.shared.CustomSelect

@Composable
fun CategoriesScreen(navController: NavHostController, drawerState: DrawerState) {
    Scaffold(
        topBar = { TopBarMenuTitle("Categorías", drawerState) },
        content = { innerPadding ->
            Column (modifier = Modifier.padding(innerPadding)) {
                val options = listOf("Option 1", "Option 2", "Option 3")
                var selectedOption by remember { mutableStateOf(options[0]) }

                CustomSelect(
                    options = options,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )
            }
        },
    )
}