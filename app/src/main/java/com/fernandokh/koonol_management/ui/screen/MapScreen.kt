package com.fernandokh.koonol_management.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle

@Composable
fun MapScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopBarGoBack("Mapa", navController) },
        content = { innerPadding ->
            Column (modifier = Modifier.padding(innerPadding)) {
                Image(
                    contentScale = ContentScale.Crop,
                    painter = painterResource(R.drawable.maps),
                    contentDescription = "mapas",
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
    )
}