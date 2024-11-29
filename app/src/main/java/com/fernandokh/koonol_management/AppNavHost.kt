package com.fernandokh.koonol_management

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
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
import com.fernandokh.koonol_management.ui.screen.categories.CategoriesScreen
import com.fernandokh.koonol_management.ui.screen.categories.CreateCategoryScreen
import com.fernandokh.koonol_management.ui.screen.categories.EditCategoryScreen
import com.fernandokh.koonol_management.ui.screen.categories.InfoCategoryScreen
import com.fernandokh.koonol_management.ui.screen.locationSalesStalls.CreateLocationSalesStallsScreen
import com.fernandokh.koonol_management.ui.screen.profile.ChangePasswordScreen
import com.fernandokh.koonol_management.ui.screen.profile.EditProfileScreen
import com.fernandokh.koonol_management.ui.screen.profile.ProfileScreen
import com.fernandokh.koonol_management.ui.screen.promotion.CreatePromotionScreen
import com.fernandokh.koonol_management.ui.screen.promotion.EditPromotionScreen
import com.fernandokh.koonol_management.ui.screen.promotion.InfoPromotionScreen
import com.fernandokh.koonol_management.ui.screen.salestalls.CreateSaleStallScreen
import com.fernandokh.koonol_management.ui.screen.salestalls.EditSaleStallScreen
import com.fernandokh.koonol_management.ui.screen.salestalls.InfoSaleStallScreen
import com.fernandokh.koonol_management.ui.screen.salestalls.SalesStallsScreen
import com.fernandokh.koonol_management.ui.screen.scheduleTianguis.CreateScheduleTianguisScreen
import com.fernandokh.koonol_management.ui.screen.sellers.CreateSellersScreen
import com.fernandokh.koonol_management.ui.screen.sellers.EditSellersScreen
import com.fernandokh.koonol_management.ui.screen.sellers.InfoSellersScreen
import com.fernandokh.koonol_management.ui.screen.sellers.SellersScreen
import com.fernandokh.koonol_management.ui.screen.tianguis.CreateTianguisScreen
import com.fernandokh.koonol_management.ui.screen.tianguis.EditTianguisScreen
import com.fernandokh.koonol_management.ui.screen.tianguis.InfoTianguisScreen
import com.fernandokh.koonol_management.ui.screen.tianguis.TianguisScreen
import com.fernandokh.koonol_management.ui.screen.users.CreateUserScreen
import com.fernandokh.koonol_management.ui.screen.users.EditUserScreen
import com.fernandokh.koonol_management.ui.screen.users.InfoUserScreen
import com.fernandokh.koonol_management.ui.screen.users.UsersScreen
import com.fernandokh.koonol_management.viewModel.AuthViewModel
import com.fernandokh.koonol_management.viewModel.AuthViewModelFactory
import com.fernandokh.koonol_management.viewModel.tianguis.EditTianguisViewModel

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

    object EditSaleStall : Screen("sales-stalls/edit/{salesStallId}"){
        fun createRoute(salesStallId: String) = "sales-stalls/edit/$salesStallId"
    }
    object InfoSaleStall : Screen("sales-stalls/info/{salesStallId}"){
        fun createRoute(salesStallId: String) = "sales-stalls/info/$salesStallId"
    }
    object CreateSaleStall : Screen("sales-stalls/create")

    object EditPromotion : Screen("promotions/edit/{promotionId}") {
        fun createRoute(promotionId: String) = "promotions/edit/$promotionId"
    }
    object InfoPromotion : Screen("promotions/info/{promotionId}") {
        fun createRoute(promotionId: String) = "promotions/info/$promotionId"
    }
    object CreatePromotion : Screen("promotions/create")

    //Tianguis
    object EditTianguis : Screen("tianguis/edit/{tianguisId}") {
        fun createRoute(tianguisId: String) = "tianguis/edit/$tianguisId"
    }
    object InfoTianguis : Screen("tianguis/info/{tianguisId}") {
        fun createRoute(tianguisId: String) = "tianguis/info/$tianguisId"
    }
    object CreateTianguis : Screen("tianguis/create")

    object CreateScheduleTianguis : Screen("schedule-tianguis/create")

    object CreateLocationSalesStalls : Screen("location-sales-stalls/create/{salesStallId}") {
        fun createRoute(salesStallId: String) = "location-sales-stalls/create/$salesStallId"
    }

}

fun NavGraphBuilder.protectedComposable(
    route: String,
    navController: NavHostController,
    tokenManager: TokenManager,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(route, arguments) { backStackEntry ->
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
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(tokenManager)
    )
    val editTianguisViewModel: EditTianguisViewModel = viewModel() // Crear instancia del ViewModel

    NavHost(navController, startDestination = Screen.Login.route, modifier = modifier) {
        composable(Screen.Login.route) { LoginScreen(navController, tokenManager) }
        protectedComposable(Screen.Menu.route, navController, tokenManager) { MenuScreen(navController) }
        protectedComposable(Screen.Users.route, navController, tokenManager) { UsersScreen(navController, drawerState) }
        protectedComposable(Screen.Tianguis.route, navController, tokenManager) { TianguisScreen(navController, drawerState) }
        protectedComposable(Screen.SalesStalls.route, navController, tokenManager) { SalesStallsScreen(navController, drawerState) }
        protectedComposable(Screen.Promotions.route, navController, tokenManager) { PromotionsScreen(navController, drawerState, tokenManager) }
        protectedComposable(Screen.Categories.route, navController, tokenManager) { CategoriesScreen(navController, drawerState, tokenManager) }
        protectedComposable(Screen.Profile.route, navController, tokenManager) { ProfileScreen(navController, drawerState, tokenManager) }
        protectedComposable(Screen.EditProfile.route, navController, tokenManager) { EditProfileScreen(navController, tokenManager) }
        protectedComposable(Screen.ChangePassword.route, navController, tokenManager) { ChangePasswordScreen(navController, tokenManager) }
        protectedComposable(Screen.CreateScheduleTianguis.route, navController, tokenManager) { CreateScheduleTianguisScreen(navController) }
        protectedComposable(
            Screen.EditSeller.route, navController, tokenManager,
            arguments = listOf(navArgument("sellerId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditSellersScreen(navController, backStackEntry.arguments?.getString("sellerId"))
        }
        protectedComposable(
            Screen.InfoSeller.route, navController, tokenManager,
            arguments = listOf(navArgument("sellerId") { type = NavType.StringType })
        ) { backStackEntry ->
            InfoSellersScreen(navController, backStackEntry.arguments?.getString("sellerId"))
        }
        protectedComposable(Screen.CreateSeller.route, navController, tokenManager) { CreateSellersScreen(navController) }
        protectedComposable(Screen.Sellers.route, navController, tokenManager) { SellersScreen(navController, drawerState) }

        protectedComposable(
            Screen.EditUser.route, navController, tokenManager,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditUserScreen(navController, backStackEntry.arguments?.getString("userId"))
        }
        protectedComposable(
            Screen.InfoUser.route, navController, tokenManager,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry -> InfoUserScreen(navController, backStackEntry.arguments?.getString("userId")) }
        protectedComposable(Screen.CreateUser.route, navController, tokenManager) { CreateUserScreen(navController) }


        protectedComposable(
            Screen.EditTianguis.route, navController, tokenManager,
            arguments = listOf(navArgument("tianguisId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tianguisId = backStackEntry.arguments?.getString("tianguisId")
            EditTianguisScreen(navController, tianguisId, authViewModel, editTianguisViewModel)
        }

        protectedComposable(
            Screen.InfoTianguis.route, navController, tokenManager,
            arguments = listOf(navArgument("tianguisId") { type = NavType.StringType })
        ) { backStackEntry ->
            InfoTianguisScreen(navController, backStackEntry.arguments?.getString("tianguisId"))
        }

        protectedComposable(Screen.CreateTianguis.route, navController, tokenManager) {
            CreateTianguisScreen(navController, authViewModel)
        }

        protectedComposable(
            Screen.EditCategory.route, navController, tokenManager,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditCategoryScreen(navController, backStackEntry.arguments?.getString("categoryId"), tokenManager)
        }
        protectedComposable(
            Screen.InfoCategory.route, navController, tokenManager,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry -> InfoCategoryScreen(navController, backStackEntry.arguments?.getString("categoryId"), tokenManager) }
        protectedComposable(Screen.CreateCategory.route, navController, tokenManager) { CreateCategoryScreen(navController, tokenManager) }

        protectedComposable(Screen.EditSaleStall.route, navController, tokenManager,
            arguments = listOf(navArgument("salesStallId") { type = NavType.StringType })
        ) {
            backStackEntry ->
            EditSaleStallScreen(navController, backStackEntry.arguments?.getString("salesStallId"), tokenManager)
        }
        protectedComposable(Screen.InfoSaleStall.route, navController, tokenManager,
            arguments = listOf(navArgument("salesStallId") { type = NavType.StringType })
        ) { backStackEntry ->
            InfoSaleStallScreen(navController, backStackEntry.arguments?.getString("salesStallId"))
        }
        protectedComposable(Screen.CreateSaleStall.route, navController, tokenManager) { CreateSaleStallScreen(navController, tokenManager) }
        protectedComposable(
            Screen.EditPromotion.route, navController, tokenManager,
            arguments = listOf(navArgument("promotionId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditPromotionScreen(navController, backStackEntry.arguments?.getString("promotionId"), tokenManager)
        }
        protectedComposable(
            Screen.InfoPromotion.route, navController, tokenManager,
            arguments = listOf(navArgument("promotionId") { type = NavType.StringType })
        ) { backStackEntry -> InfoPromotionScreen(navController, backStackEntry.arguments?.getString("promotionId"), tokenManager) }
        protectedComposable(Screen.CreatePromotion.route, navController, tokenManager) { CreatePromotionScreen(navController, tokenManager) }

        protectedComposable(
            Screen.CreateLocationSalesStalls.route, navController, tokenManager,
            arguments = listOf(navArgument("salesStallId") { type = NavType.StringType })
        ) {
            backStackEntry ->
            CreateLocationSalesStallsScreen(navController, backStackEntry.arguments?.getString("salesStallId"))
        }
    }
}