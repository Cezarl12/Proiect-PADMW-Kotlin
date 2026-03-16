package com.example.mealsappkotlin.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mealsappkotlin.ui.AuthViewModel
import com.example.mealsappkotlin.ui.FavouriteViewModel
import com.example.mealsappkotlin.ui.FavouriteViewModelFactory
import com.example.mealsappkotlin.ui.components.AppHeader
import com.example.mealsappkotlin.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfilePage(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current
    val favViewModel: FavouriteViewModel = viewModel(factory = FavouriteViewModelFactory(context))
    val favourites by favViewModel.favourites.collectAsState()
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) { favViewModel.loadFavourites() }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Profile", navController = navController, showBack = true)

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.size(90.dp).clip(CircleShape),
                color = Color(0xFF1A1A2E)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        firebaseUser?.email?.first()?.uppercaseChar()?.toString() ?: "?",
                        fontSize = 36.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val displayName = firebaseUser?.email?.substringBefore("@") ?: "Utilizator"
            Text(displayName, fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(4.dp))

            Text(firebaseUser?.email ?: "", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${favourites.size}", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    Text("Rețete favorite", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    authViewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Deconectează-te", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}