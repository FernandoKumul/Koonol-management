package com.fernandokh.koonol_management

import android.util.Log
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.screen.LoginScreen
import com.fernandokh.koonol_management.ui.screen.MenuScreen
import com.fernandokh.koonol_management.ui.screen.PromotionsScreen
import com.fernandokh.koonol_management.ui.screen.SalesStallsScreen
import com.fernandokh.koonol_management.ui.screen.TianguisScreen
import com.fernandokh.koonol_management.ui.screen.categories.CategoriesScreen
import com.fernandokh.koonol_management.ui.screen.categories.CreateCategoryScreen
import com.fernandokh.koonol_management.ui.screen.categories.EditCategoryScreen
import com.fernandokh.koonol_management.ui.screen.categories.InfoCategoryScreen
import com.fernandokh.koonol_management.ui.screen.profile.ChangePasswordScreen
import com.fernandokh.koonol_management.ui.screen.profile.EditProfileScreen
import com.fernandokh.koonol_management.ui.screen.profile.ProfileScreen
import com.fernandokh.koonol_management.ui.screen.sellers.CreateSellersScreen
import com.fernandokh.koonol_management.ui.screen.sellers.EditSellersScreen
import com.fernandokh.koonol_management.ui.screen.sellers.InfoSellersScreen
import com.fernandokh.koonol_management.ui.screen.sellers.SellersScreen
import com.fernandokh.koonol_management.ui.screen.users.CreateUserScreen
import com.fernandokh.koonol_management.ui.screen.users.EditUserScreen
import com.fernandokh.koonol_management.ui.screen.users.InfoUserScreen
import com.fernandokh.koonol_management.ui.screen.users.UsersScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Menu : Screen("menu")
    object Profile : Screen("profile")
    object EditProfile : Screen("profile/edit")
    object ChangePassword : Screen("change-password")
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

    object EditCategory : Screen("category/edit/{categoryId}") {
        fun createRoute(categoryId: String) = "category/edit/$categoryId"
    }
    object InfoCategory : Screen("category/info/{categoryId}") {
        fun createRoute(categoryId: String) = "category/info/$categoryId"
    }
    object CreateCategory : Screen("category/create")
}

fun NavGraphBuilder.protectedComposable(
    route: String,
    navController: NavHostController,
    tokenManager: TokenManager,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(route) { backStackEntry ->
        var isTokenValid by remember { mutableStateOf<Boolean?>(null) }
        var hasNavigated by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            isTokenValid = tokenManager.isTokenValid()
        }
        when (isTokenValid) {
            true -> {
                content(backStackEntry)
            }
            false -> {
                if (!hasNavigated) {
                    hasNavigated = true
                    navController.navigate(Screen.Login.route) {
                        popUpTo(route)
                    }
                }
            }
            null -> {
            }
        }
    }
}





@Composable
fun AppNavHost(
    modifier: Modifier = Modifier, navController: NavHostController, drawerState: DrawerState, tokenManager: TokenManager
) {
    NavHost(navController, startDestination = Screen.Login.route, modifier = modifier) {
        composable(Screen.Login.route) { LoginScreen(navController, tokenManager) }
        protectedComposable(Screen.Menu.route, navController, tokenManager) { MenuScreen(navController) }
        protectedComposable(Screen.Users.route, navController, tokenManager) { UsersScreen(navController, drawerState) }
        protectedComposable(Screen.Tianguis.route, navController, tokenManager) { TianguisScreen(navController, drawerState) }
        protectedComposable(Screen.SalesStalls.route, navController, tokenManager) { SalesStallsScreen(navController, drawerState) }
        protectedComposable(Screen.Promotions.route, navController, tokenManager) { PromotionsScreen(navController, drawerState) }
        protectedComposable(Screen.Categories.route, navController, tokenManager) { CategoriesScreen(navController, drawerState) }
        protectedComposable(Screen.Profile.route, navController, tokenManager) { ProfileScreen(navController, drawerState, tokenManager) }
        protectedComposable(Screen.EditProfile.route, navController, tokenManager) { EditProfileScreen(navController, tokenManager) }
        protectedComposable(Screen.ChangePassword.route, navController, tokenManager) { ChangePasswordScreen(navController, tokenManager) }
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
        composable(
            Screen.EditCategory.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditCategoryScreen(navController, backStackEntry.arguments?.getString("categoryId"))
        }
        composable(
            Screen.InfoCategory.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry -> InfoCategoryScreen(navController, backStackEntry.arguments?.getString("categoryId")) }
        composable(Screen.CreateCategory.route) { CreateCategoryScreen(navController) }
    }
}