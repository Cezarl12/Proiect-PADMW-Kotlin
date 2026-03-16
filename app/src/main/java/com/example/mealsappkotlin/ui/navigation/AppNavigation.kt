package com.example.mealsappkotlin.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mealsappkotlin.repository.AuthService
import com.example.mealsappkotlin.ui.screens.explore.ExplorePage
import com.example.mealsappkotlin.ui.screens.favourite.FavouritePage
import com.example.mealsappkotlin.ui.screens.home.HomePage
import com.example.mealsappkotlin.ui.screens.login.LoginPage
import com.example.mealsappkotlin.ui.screens.meal.MealPage
import com.example.mealsappkotlin.ui.screens.profile.ProfilePage
import com.example.mealsappkotlin.ui.screens.register.RegisterPage
import com.example.mealsappkotlin.ui.screens.results.ResultsPage

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Explore : Screen("explore")
    object Favourite : Screen("favourite")
    object Profile : Screen("profile")
    object Login : Screen("login")
    object Register : Screen("register")
    object Meal : Screen("meal/{mealId}") {
        fun createRoute(mealId: String) = "meal/$mealId"
    }
    object ResultsByCategory : Screen("results/category/{category}") {
        fun createRoute(category: String) = "results/category/$category"
    }
    object ResultsByQuery : Screen("results/search/{query}") {
        fun createRoute(query: String) = "results/search/$query"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val authService = remember { AuthService() }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomePage(navController) }
        composable(Screen.Explore.route) { ExplorePage(navController) }
        composable(Screen.Login.route) { LoginPage(navController) }
        composable(Screen.Register.route) { RegisterPage(navController) }
        composable(Screen.Meal.route) { backStackEntry ->
            MealPage(navController, backStackEntry.arguments?.getString("mealId") ?: "")
        }
        composable(Screen.ResultsByCategory.route) { backStackEntry ->
            ResultsPage(navController, category = backStackEntry.arguments?.getString("category"), query = null)
        }
        composable(Screen.ResultsByQuery.route) { backStackEntry ->
            ResultsPage(navController, category = null, query = backStackEntry.arguments?.getString("query"))
        }

        // Protected routes
        composable(Screen.Favourite.route) {
            if (authService.isLoggedIn()) {
                FavouritePage(navController)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Favourite.route) { inclusive = true }
                    }
                }
            }
        }
        composable(Screen.Profile.route) {
            if (authService.isLoggedIn()) {
                ProfilePage(navController)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            }
        }
    }
}