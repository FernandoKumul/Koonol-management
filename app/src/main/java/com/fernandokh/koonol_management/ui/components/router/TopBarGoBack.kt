package com.fernandokh.koonol_management.ui.components.router

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarGoBack(title: String, navController: NavHostController) {
    var canNavigate by remember { mutableStateOf(true) }

    Log.i("TestNav", canNavigate.toString())
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        title = {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                if (canNavigate) {
                    canNavigate = false
                    navController.popBackStack()
                }
            }, enabled = canNavigate
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back"
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun PrevTopBarMenuTitle() {
    val navController = rememberNavController()
    KoonolmanagementTheme (dynamicColor = false) {
        TopBarGoBack("Preview", navController)
    }
}