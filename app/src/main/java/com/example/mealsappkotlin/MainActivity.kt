package com.example.mealsappkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mealsappkotlin.ui.components.BottomNavBar
import com.example.mealsappkotlin.ui.navigation.AppNavigation
import com.example.mealsappkotlin.ui.navigation.Screen
import com.example.mealsappkotlin.ui.theme.MealsAppKotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MealsAppKotlinTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val snackbarHostState = remember { SnackbarHostState() }
                // Scope la nivel de Activity — supraviețuiește navigării între ecrane,
                // așa că snackbar-ul are timp să se afișeze chiar și după navController.navigate(...)
                val mainScope = rememberCoroutineScope()
                val showSnackbar: (String) -> Unit = { msg ->
                    mainScope.launch { snackbarHostState.showSnackbar(msg) }
                }

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Explore.route,
                    Screen.Favourite.route,
                    Screen.Profile.route
                )

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        snackbarHostState = snackbarHostState,
                        showSnackbar = showSnackbar
                    )
                }
            }
        }
    }
}