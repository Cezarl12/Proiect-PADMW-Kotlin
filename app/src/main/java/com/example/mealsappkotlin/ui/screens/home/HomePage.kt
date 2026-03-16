package com.example.mealsappkotlin.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mealsappkotlin.ui.AuthViewModel
import com.example.mealsappkotlin.ui.MealViewModel
import com.example.mealsappkotlin.ui.navigation.Screen

@Composable
fun HomePage(navController: NavController) {
    val mealViewModel: MealViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val randomMeal by mealViewModel.randomMeal.collectAsState()
    val isLoading by mealViewModel.isLoading.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(Unit) { mealViewModel.loadRandomMeal() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🍴", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("RecipeHub", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Surface(
                modifier = Modifier.size(40.dp).clickable {
                    if (isLoggedIn) navController.navigate(Screen.Profile.route)
                    else navController.navigate(Screen.Login.route)
                },
                shape = RoundedCornerShape(50),
                color = Color(0xFF1A1A2E)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("C", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Descoperă", fontSize = 28.sp, fontWeight = FontWeight.Normal)
        Text("noi rețete", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("✦", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text("SPECIALITATEA ZILEI", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            randomMeal?.let { meal ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { navController.navigate(Screen.Meal.createRoute(meal.idMeal)) }
                ) {
                    AsyncImage(
                        model = meal.strMealThumb,
                        contentDescription = meal.strMeal,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Category chip
                    Surface(
                        modifier = Modifier.padding(12.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.9f)
                    ) {
                        Text(
                            meal.strCategory,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Arrow button
                    Surface(
                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(36.dp),
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.9f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("↗", fontSize = 16.sp)
                        }
                    }

                    // Bottom info
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                    ) {
                        Text(
                            "${meal.strArea.uppercase()} · RETETA ZILEI",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            meal.strMeal,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Explore button
        Button(
            onClick = { navController.navigate(Screen.Explore.route) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A2E))
        ) {
            Text("Explorează Rețete", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Filled.Search, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("INSPIRAT DE PASIUNE CULINARĂ", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}