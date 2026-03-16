package com.example.mealsappkotlin.ui.screens.favourite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mealsappkotlin.ui.FavouriteViewModel
import com.example.mealsappkotlin.ui.FavouriteViewModelFactory
import com.example.mealsappkotlin.ui.components.AppHeader
import com.example.mealsappkotlin.ui.navigation.Screen

@Composable
fun FavouritePage(navController: NavController) {
    val context = LocalContext.current
    val favViewModel: FavouriteViewModel = viewModel(factory = FavouriteViewModelFactory(context))
    val favourites by favViewModel.favourites.collectAsState()

    LaunchedEffect(Unit) { favViewModel.loadFavourites() }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Favorite", navController = navController)

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Text("Rețetele tale", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${favourites.size} rețete salvate", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            if (favourites.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nu ai rețete favorite", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favourites) { meal ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { navController.navigate(Screen.Meal.createRoute(meal.idMeal)) }
                        ) {
                            AsyncImage(
                                model = meal.strMealThumb,
                                contentDescription = meal.strMeal,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Gradient
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                            startY = 80f
                                        )
                                    )
                            )

                            IconButton(
                                onClick = { favViewModel.toggle(meal) },
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    meal.strCategory.uppercase(),
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold
                                )
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
}