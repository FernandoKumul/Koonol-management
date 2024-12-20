package com.fernandokh.koonol_management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.fernandokh.koonol_management.data.models.UserPreviewModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.components.router.RouteListMenu
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.routes
import com.fernandokh.koonol_management.viewModel.AuthViewModel
import com.fernandokh.koonol_management.viewModel.AuthViewModelFactory
import com.fernandokh.koonol_management.viewModel.NavigationEvent
import com.fernandokh.koonol_management.viewModel.profile.ProfileViewModel
import com.fernandokh.koonol_management.viewModel.profile.ProfileViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val tokenManager = TokenManager(applicationContext)
        enableEdgeToEdge()
        setContent {
            KoonolmanagementTheme {

                LaunchedEffect(true) {
                    delay(5000)
                    //onTimeout()
                }

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                    {
                        KoonolmanagementTheme(dynamicColor = false) {
                            Surface(color = MaterialTheme.colorScheme.background) {
                                MyApp(
                                    tokenManager = tokenManager
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyApp(
    tokenManager: TokenManager
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    //Controla el menu lateral
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { SideMenu(navController, drawerState, tokenManager) }
        },
        gesturesEnabled = enabledMenu(navBackStackEntry?.destination?.route)
    ) {
        AppNavHost(
            navController = navController,
            drawerState = drawerState,
            tokenManager = tokenManager
        )
    }
}

fun enabledMenu(route: String?): Boolean {
    return when (route) {
        Screen.Menu.route -> {
            false
        }

        Screen.Login.route -> {
            false
        }

        null -> {
            false
        }

        else -> {
            true
        }
    }
}

@Composable
fun SideMenu(
    navController: NavHostController,
    drawerState: DrawerState,
    tokenManager: TokenManager
) {

    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(tokenManager)
    )

    val isUser by viewModel.isPreview.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        tokenManager.accessToken.collect { token ->
            viewModel.getPreview(token)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.8f)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(0.dp, 16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                isUser == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontró el usuario",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                else -> {
                    UserDetails(isUser!!)
                }
            }
            RouteListMenu(routes = routes, navController, drawerState)
        }

        BtnLogout(navController, drawerState, tokenManager)
    }
}

@Composable
fun BtnLogout(
    navController: NavHostController,
    drawerState: DrawerState,
    tokenManager: TokenManager
) {
    val scope = rememberCoroutineScope()
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(tokenManager)
    )
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.AuthSuccess -> {
                    navController.navigate(Screen.Login.route)
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .clickable {
                viewModel.logout()

                scope.launch {
                    drawerState.close()
                }
            }
            .padding(12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_logout_circle),
            contentDescription = "Logout",
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = "Cerrar sesión",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun UserDetails(user: UserPreviewModel) {
    Row(
        Modifier.padding(16.dp, 12.dp, 16.dp, 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (user.photo != null) {
            AsyncImage(
                model = user.photo,
                contentDescription = "img_user",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                placeholder = painterResource(R.drawable.default_user)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.default_user),
                contentDescription = "img_user",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
        }

        Column {
            Text(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 10.dp),
                text = user.name,
            )
            Text(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = user.email,
                fontSize = 13.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrevMyApp() {
    MyApp(
        tokenManager = TokenManager(LocalContext.current)
    )
}

//@Preview(showBackground = true, showSystemUi = false)
//@Composable
//fun PrevSideMenu() {
//    SideMenu(
//        navController = rememberNavController(),
//        rememberDrawerState(initialValue = DrawerValue.Closed)
//    )
//}