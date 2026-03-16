package com.example.mealsappkotlin.repository

import android.content.Context
import com.example.mealsappkotlin.model.Meal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await

class FavouriteService(
    private val authService: AuthService,
    private val context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("favourites", Context.MODE_PRIVATE)

    private fun userId() = authService.getCurrentUserId()

    private fun cacheKey() = "fav_${userId()}"

    private fun saveCache(meals: List<Meal>) {
        prefs.edit().putString(cacheKey(), gson.toJson(meals)).apply()
    }

    private fun loadCache(): List<Meal> {
        val json = prefs.getString(cacheKey(), "[]") ?: "[]"
        val type = object : TypeToken<List<Meal>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getAll(): List<Meal> {
        val uid = userId() ?: return emptyList()
        return try {
            val snapshot = firestore.collection("favourites")
                .document(uid)
                .collection("meals")
                .get()
                .await()
            val meals = snapshot.documents.mapNotNull { it.toObject(Meal::class.java) }
            saveCache(meals)
            meals
        } catch (e: Exception) {
            loadCache()
        }
    }

    suspend fun add(meal: Meal) {
        val uid = userId() ?: return
        val updated = loadCache().toMutableList()
        if (updated.none { it.idMeal == meal.idMeal }) updated.add(meal)
        saveCache(updated)
        try {
            firestore.collection("favourites")
                .document(uid)
                .collection("meals")
                .document(meal.idMeal)
                .set(meal)
                .await()
        } catch (e: Exception) { }
    }

    suspend fun remove(mealId: String) {
        val uid = userId() ?: return
        val updated = loadCache().filter { it.idMeal != mealId }
        saveCache(updated)
        try {
            firestore.collection("favourites")
                .document(uid)
                .collection("meals")
                .document(mealId)
                .delete()
                .await()
        } catch (e: Exception) { }
    }

    suspend fun isFavourite(mealId: String): Boolean {
        return loadCache().any { it.idMeal == mealId }
    }

    suspend fun toggle(meal: Meal) {
        if (isFavourite(meal.idMeal)) remove(meal.idMeal) else add(meal)
    }
}