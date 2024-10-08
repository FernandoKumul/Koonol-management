package com.fernandokh.koonol_management

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoonolmanagementTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val topBarState = rememberSaveable { (mutableStateOf(true)) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    when (navBackStackEntry?.destination?.route) {
        Screen.Menu.route -> {
            topBarState.value = false
        }
        Screen.Login.route -> {
            topBarState.value = false
        }
        else -> {
            topBarState.value = true
        }
    }

    Scaffold(
        topBar = {
            if (topBarState.value) {
                Text("TopBar")
            }
        },
        content = { innerPadding ->
            AppNavHost(
                modifier = Modifier.padding(innerPadding).background(color = Color.Red),
                navController = navController
            )
        }
    )
}

@Preview
@Composable
fun PrevMyApp() {
    MyApp()
}