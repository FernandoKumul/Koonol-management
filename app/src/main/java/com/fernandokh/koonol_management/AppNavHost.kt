package com.fernandokh.koonol_management

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.screen.CategoriesScreen
import com.fernandokh.koonol_management.ui.screen.LoginScreen
import com.fernandokh.koonol_management.ui.screen.MenuScreen
import com.fernandokh.koonol_management.ui.screen.ProfileScreen
import com.fernandokh.koonol_management.ui.screen.PromotionsScreen
import com.fernandokh.koonol_management.ui.screen.SalesStallsScreen
import com.fernandokh.koonol_management.ui.screen.sellers.SellersScreen
import com.fernandokh.koonol_management.ui.screen.TianguisScreen
import com.fernandokh.koonol_management.ui.screen.users.EditUserScreen
import com.fernandokh.koonol_management.ui.screen.sellers.CreateSellersScreen
import com.fernandokh.koonol_management.ui.screen.sellers.EditSellersScreen
import com.fernandokh.koonol_management.ui.screen.sellers.InfoSellersScreen
import com.fernandokh.koonol_management.ui.screen.users.CreateUserScreen
import com.fernandokh.koonol_management.ui.screen.users.InfoUserScreen
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
    object EditSeller : Screen("sellers/edit/{sellerId}") {
        fun createRoute(sellerId: String) = "sellers/edit/$sellerId"
    }
    object InfoSeller : Screen("sellers/info/{sellerId}") {
        fun createRoute(sellerId: String) = "sellers/info/$sellerId"
    }
    object CreateSeller : Screen("sellers/create")

    object EditUser : Screen("users/edit/{userId}") {
        fun createRoute(userId: String) = "users/edit/$userId"
    }
    object InfoUser : Screen("users/info/{userId}") {
        fun createRoute(userId: String) = "users/info/$userId"
    }
    object CreateUser : Screen("users/create")
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier, navController: NavHostController, drawerState: DrawerState, tokenManager: TokenManager
) {
    NavHost(navController, startDestination = Screen.Login.route, modifier = modifier) {
        composable(Screen.Login.route) { LoginScreen(navController, tokenManager) }
        composable(Screen.Menu.route) { MenuScreen(navController, tokenManager) }
        composable(Screen.Users.route) { UsersScreen(navController, drawerState) }
        composable(Screen.Tianguis.route) { TianguisScreen(navController, drawerState) }
        composable(Screen.SalesStalls.route) { SalesStallsScreen(navController, drawerState) }
        composable(Screen.Promotions.route) { PromotionsScreen(navController, drawerState) }
        composable(Screen.Categories.route) { CategoriesScreen(navController, drawerState) }
        composable(Screen.Profile.route) { ProfileScreen(navController, drawerState) }
        composable(
            Screen.EditSeller.route,
            arguments = listOf(navArgument("sellerId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditSellersScreen(navController, backStackEntry.arguments?.getString("sellerId"))
        }
        composable(
            Screen.InfoSeller.route,
            arguments = listOf(navArgument("sellerId") { type = NavType.StringType })
        ) { backStackEntry -> InfoSellersScreen(navController, backStackEntry.arguments?.getString("sellerId")) }
        composable(Screen.CreateSeller.route) { CreateSellersScreen(navController) }
        composable(Screen.Sellers.route) { SellersScreen(navController, drawerState) }
        composable(
            Screen.EditUser.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditUserScreen(navController, backStackEntry.arguments?.getString("userId"))
        }
        composable(
            Screen.InfoUser.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry -> InfoUserScreen(navController, backStackEntry.arguments?.getString("userId")) }
        composable(Screen.CreateUser.route) { CreateUserScreen(navController) }
    }
}