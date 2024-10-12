package com.fernandokh.koonol_management

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fernandokh.koonol_management.ui.screen.CategoriesScreen
import com.fernandokh.koonol_management.ui.screen.LoginScreen
import com.fernandokh.koonol_management.ui.screen.MenuScreen
import com.fernandokh.koonol_management.ui.screen.ProfileScreen
import com.fernandokh.koonol_management.ui.screen.PromotionsScreen
import com.fernandokh.koonol_management.ui.screen.SalesStallsScreen
import com.fernandokh.koonol_management.ui.screen.SellersScreen
import com.fernandokh.koonol_management.ui.screen.TianguisScreen
import com.fernandokh.koonol_management.ui.screen.users.CreateUserScreen
import com.fernandokh.koonol_management.ui.screen.users.EditUserScreen
import com.fernandokh.koonol_management.ui.screen.users.UsersScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Menu : Screen("menu")
    object Profile : Screen("profile")
    object Tianguis : Screen("tianguis")
    object SalesStalls : Screen("sales-stalls")
    object Promotions : Screen("promotions")
    object Categories : Screen("categories")
    object Users : Screen("users")
    object Sellers : Screen("sellers")
    object EditUser : Screen("users/edit")
    object CreateUser : Screen("users/create")
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier, navController: NavHostController, drawerState: DrawerState
) {
    NavHost(navController, startDestination = Screen.Login.route, modifier = modifier) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Menu.route) { MenuScreen(navController) }
        composable(Screen.Users.route) { UsersScreen(navController, drawerState) }
        composable(Screen.Tianguis.route) { TianguisScreen(navController, drawerState) }
        composable(Screen.SalesStalls.route) { SalesStallsScreen(navController, drawerState) }
        composable(Screen.Promotions.route) { PromotionsScreen(navController, drawerState) }
        composable(Screen.Categories.route) { CategoriesScreen(navController, drawerState) }
        composable(Screen.Profile.route) { ProfileScreen(navController, drawerState) }
        composable(Screen.Sellers.route) { SellersScreen(navController, drawerState) }
        composable(Screen.EditUser.route) { EditUserScreen(navController) }
        composable(Screen.CreateUser.route) { CreateUserScreen(navController) }
    }
}