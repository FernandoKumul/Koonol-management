package com.fernandokh.koonol_management

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        enableEdgeToEdge()
        setContent {
            KoonolmanagementTheme {

                LaunchedEffect(true) {
                    delay(3000)
                    //onTimeout()
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                    {
                        MyApp()
                    }
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    val topBarState = rememberSaveable { (mutableStateOf(false)) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    when (navBackStackEntry?.destination?.route) {
        Screen.Menu.route -> {
            topBarState.value = false
        }

        Screen.Login.route -> {
            topBarState.value = false
        }

        null -> {
            topBarState.value = false
        }

        else -> {
            topBarState.value = true
        }
    }

    KoonolmanagementTheme(dynamicColor = false) {
        Scaffold(
            topBar = {
                if (topBarState.value) {
                    TopBar(navBackStackEntry?.destination?.route ?: "")
                }
            },
            content = { innerPadding ->
                AppNavHost(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController
                )
            }

        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(route: String){
    val title = when (route) {
        "users" -> stringResource(R.string.title_route_users)
        else -> stringResource(R.string.title_route_default)
    }

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
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description"
                )
            }
        },
    )
}

@Preview
@Composable
fun PrevMyApp() {
    MyApp()
}