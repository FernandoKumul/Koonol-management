package com.fernandokh.koonol_management

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fernandokh.koonol_management.ui.screen.LoginScreen
import com.fernandokh.koonol_management.ui.screen.MenuScreen
import com.fernandokh.koonol_management.ui.screen.UsersScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Menu : Screen("menu")
    object Users : Screen("users")
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier, navController: NavHostController
) {
    NavHost(navController, startDestination = Screen.Login.route, modifier = modifier) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Menu.route) { MenuScreen(navController) }
        composable(Screen.Users.route) { UsersScreen(navController) }
    }
}