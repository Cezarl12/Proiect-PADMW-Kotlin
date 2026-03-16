package com.example.mealsappkotlin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mealsappkotlin.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppHeader(
    title: String,
    navController: NavController,
    showBack: Boolean = false
) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val initial = firebaseUser?.email?.first()?.uppercaseChar()?.toString() ?: "?"
    val isLoggedIn = firebaseUser != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showBack) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }

        Surface(
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    if (isLoggedIn) navController.navigate(Screen.Profile.route)
                    else navController.navigate(Screen.Login.route)
                },
            shape = RoundedCornerShape(50),
            color = Color(0xFF1A1A2E)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(initial, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}