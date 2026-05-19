package com.example.mealsappkotlin.ui.screens.results

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mealsappkotlin.ui.components.AppHeader
import com.example.mealsappkotlin.ui.navigation.Screen
import com.example.mealsappkotlin.viewmodel.FavouriteViewModel
import com.example.mealsappkotlin.viewmodel.MealViewModel

/**
 * ResultsPage respectă UDF:
 * - UI trimite evenimente (loadMealsByCategory / searchMeals / loadMealDetailsForResults) → ViewModel
 * - ViewModel expune starea prin StateFlow → UI observă și se redesenează
 * - NICIUN apel direct la Retrofit sau RetrofitInstance din Composable (anti-pattern eliminat)
 */
@Composable
fun ResultsPage(navController: NavController, category: String?, query: String?) {
    val mealViewModel: MealViewModel = viewModel()
    val favViewModel: FavouriteViewModel = viewModel()

    val meals by mealViewModel.meals.collectAsState()
    val isLoading by mealViewModel.isLoading.collectAsState()
    val favouriteIds by favViewModel.favouriteIds.collectAsState()
    val mealDetails by mealViewModel.mealDetails.collectAsState()

    LaunchedEffect(category, query) {
        favViewModel.loadFavourites()
        when {
            category != null -> mealViewModel.loadMealsByCategory(category)
            query != null -> mealViewModel.searchMeals(query)
        }
    }

    // Când filtrul e by category, API-ul returnează date parțiale → cerem detalii prin ViewModel
    LaunchedEffect(meals) {
        if (meals.isNotEmpty() && category != null) {
            mealViewModel.loadMealDetailsForResults(meals.map { it.idMeal })
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(
            title = category ?: query ?: "Rezultate",
            navController = navController,
            showBack = true
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(meals) { meal ->
                    val detail = mealDetails[meal.idMeal]
                    val displayCategory = when {
                        meal.strCategory.isNotBlank() -> meal.strCategory
                        detail?.strCategory?.isNotBlank() == true -> detail.strCategory
                        category != null -> category
                        else -> ""
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                navController.navigate(Screen.Meal.createRoute(meal.idMeal))
                            }
                    ) {
                        AsyncImage(
                            model = meal.strMealThumb,
                            contentDescription = meal.strMeal,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                        startY = 60f
                                    )
                                )
                        )

                        IconButton(
                            onClick = {
                                val mealToToggle = detail ?: meal
                                favViewModel.toggle(mealToToggle)
                            },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                if (favouriteIds.contains(meal.idMeal)) Icons.Filled.Favorite
                                else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                tint = if (favouriteIds.contains(meal.idMeal)) Color.Red else Color.White
                            )
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        ) {
                            if (displayCategory.isNotBlank()) {
                                Text(
                                    displayCategory.uppercase(),
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                meal.strMeal,
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
