package com.example.mealsappkotlin.ui.screens.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mealsappkotlin.ui.MealViewModel
import com.example.mealsappkotlin.ui.components.AppHeader
import com.example.mealsappkotlin.ui.navigation.Screen

@Composable
fun ExplorePage(navController: NavController) {
    val mealViewModel: MealViewModel = viewModel()
    val categories by mealViewModel.categories.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { mealViewModel.loadCategories() }

    fun doSearch() {
        if (searchQuery.isNotEmpty()) {
            navController.navigate(Screen.ResultsByQuery.createRoute(searchQuery))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Explorează", navController = navController)

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Text("Găsește rețeta", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("perfectă", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Caută după nume sau alege o categorie", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Caută rețete") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { doSearch() }),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        TextButton(onClick = { doSearch() }) { Text("Caută") }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text("CATEGORII", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.ResultsByCategory.createRoute(category.strCategory))
                        }
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = category.strCategoryThumb,
                                contentDescription = category.strCategory,
                                modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = category.strCategory,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}