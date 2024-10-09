package com.fernandokh.koonol_management

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
import com.fernandokh.koonol_management.ui.screen.UsersScreen

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
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier, navController: NavHostController
) {
    NavHost(navController, startDestination = Screen.Login.route, modifier = modifier) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Menu.route) { MenuScreen(navController) }
        composable(Screen.Users.route) { UsersScreen(navController) }
        composable(Screen.Tianguis.route) { TianguisScreen(navController) }
        composable(Screen.SalesStalls.route) { SalesStallsScreen(navController) }
        composable(Screen.Promotions.route) { PromotionsScreen(navController) }
        composable(Screen.Categories.route) { CategoriesScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.Sellers.route) { SellersScreen(navController) }
    }
}