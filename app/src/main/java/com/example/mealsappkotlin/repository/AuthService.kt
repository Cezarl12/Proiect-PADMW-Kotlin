package com.example.mealsappkotlin.repository

import com.example.mealsappkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun register(name: String, email: String, password: String): Boolean {
        return try {
            val cred = auth.createUserWithEmailAndPassword(email, password).await()
            firestore.collection("users").document(cred.user!!.uid).set(
                mapOf("id" to cred.user!!.uid, "name" to name, "email" to email)
            ).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun login(email: String, password: String): User? {
        return try {
            val cred = auth.signInWithEmailAndPassword(email, password).await()
            val doc = firestore.collection("users").document(cred.user!!.uid).get().await()
            User(
                id = doc.getString("id") ?: "",
                name = doc.getString("name") ?: "",
                email = doc.getString("email") ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}