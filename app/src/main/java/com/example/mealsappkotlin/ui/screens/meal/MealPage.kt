package com.example.mealsappkotlin.ui.screens.meal

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mealsappkotlin.ui.FavouriteViewModel
import com.example.mealsappkotlin.ui.FavouriteViewModelFactory
import com.example.mealsappkotlin.ui.MealViewModel
import com.example.mealsappkotlin.ui.components.AppHeader

@Composable
fun MealPage(navController: NavController, mealId: String) {
    val mealViewModel: MealViewModel = viewModel()
    val context = LocalContext.current
    val favViewModel: FavouriteViewModel = viewModel(factory = FavouriteViewModelFactory(context))
    val meal by mealViewModel.selectedMeal.collectAsState()
    val isLoading by mealViewModel.isLoading.collectAsState()
    val favouriteIds by favViewModel.favouriteIds.collectAsState()

    LaunchedEffect(mealId) {
        mealViewModel.loadMealById(mealId)
        favViewModel.loadFavourites()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    meal?.let { m ->
        val ingredients = (1..20).mapNotNull { i ->
            val ingredient = when(i) {
                1 -> m.strIngredient1; 2 -> m.strIngredient2; 3 -> m.strIngredient3
                4 -> m.strIngredient4; 5 -> m.strIngredient5; 6 -> m.strIngredient6
                7 -> m.strIngredient7; 8 -> m.strIngredient8; 9 -> m.strIngredient9
                10 -> m.strIngredient10; else -> null
            }
            val measure = when(i) {
                1 -> m.strMeasure1; 2 -> m.strMeasure2; 3 -> m.strMeasure3
                4 -> m.strMeasure4; 5 -> m.strMeasure5; 6 -> m.strMeasure6
                7 -> m.strMeasure7; 8 -> m.strMeasure8; 9 -> m.strMeasure9
                10 -> m.strMeasure10; else -> null
            }
            if (!ingredient.isNullOrBlank()) Pair(ingredient.trim(), measure?.trim() ?: "") else null
        }

        val steps = m.strInstructions
            .split(Regex("""\r\n\r\n|\n\n|(?<=\.)\s+(?=[A-Z])"""))
            .map { it.replace(Regex("""\r\n|\n"""), " ").trim() }
            .filter { it.length > 20 }

        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(title = m.strMeal, navController = navController, showBack = true)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box {
                    AsyncImage(
                        model = m.strMealThumb,
                        contentDescription = m.strMeal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                    startY = 100f
                                )
                            )
                    )

                    IconButton(
                        onClick = { favViewModel.toggle(m) },
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    ) {
                        Icon(
                            if (favouriteIds.contains(m.idMeal)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = if (favouriteIds.contains(m.idMeal)) Color.Red else Color.White
                        )
                    }

                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                    ) {
                        Text(
                            "${m.strArea.uppercase()} • ${m.strCategory.uppercase()}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            m.strMeal,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {

                    // YouTube button
                    if (!m.strYoutube.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(m.strYoutube))
                                context.startActivity(intent)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Red,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("▶", color = Color.White, fontSize = 16.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Urmărește rețeta", fontSize = 12.sp, color = Color.Gray)
                                    Text("Vezi pe YouTube", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text("›", fontSize = 20.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Ingrediente", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    ingredients.forEach { (ingredient, measure) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(8.dp),
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.primary
                                ) {}
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(ingredient, fontSize = 14.sp)
                            }
                            Text(measure, fontSize = 13.sp, color = Color.Gray)
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Instrucțiuni", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFF1A1A2E),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "${index + 1}",
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                step.trim(),
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}