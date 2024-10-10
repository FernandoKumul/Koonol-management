package com.fernandokh.koonol_management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import kotlinx.coroutines.launch
import com.fernandokh.koonol_management.ui.components.router.RouteListMenu
import com.fernandokh.koonol_management.utils.routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val topBarState = rememberSaveable { (mutableStateOf(false)) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet { SideMenu(navController, drawerState) }
            },
            gesturesEnabled = topBarState.value
        ) {
            Scaffold(
                topBar = {
                    if (topBarState.value) {
                        TopBar(navBackStackEntry?.destination?.route, drawerState)
                    }
                },
                content = { innerPadding ->
                    AppNavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController
                    )
                },
            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(route: String?, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

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
            IconButton(onClick = {
                scope.launch {
                    drawerState.apply {
                        if (isClosed) open() else close()
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description"
                )
            }
        },
    )
}

@Composable
fun SideMenu(navController: NavHostController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxHeight()
            .fillMaxWidth(0.8f)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(0.dp, 16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                Modifier.padding(16.dp, 12.dp, 16.dp, 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Image(
                    painter = painterResource(R.drawable.default_user),
                    contentDescription = "img_user",
                    modifier = Modifier.size(64.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        text = "Jonh Doe Default"
                    )
                    Text(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = "defaultemail@gmail.com",
                        fontSize = 13.sp
                    )
                }
            }
            RouteListMenu(routes = routes, navController, drawerState)
        }

        Row(
            modifier = Modifier
                .clickable {
                    navController.navigate(Screen.Login.route)
                    scope.launch {
                        drawerState.apply {
                            close()
                        }
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
                text ="Cerrar sesión",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview
@Composable
fun PrevMyApp() {
    MyApp()
}

@Preview(showBackground = true)
@Composable
fun PrevSideMenu() {
    SideMenu(navController = rememberNavController(), rememberDrawerState(initialValue = DrawerValue.Closed))
}