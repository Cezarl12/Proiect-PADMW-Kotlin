package com.example.mealsappkotlin.repository

import android.content.Context
import com.example.mealsappkotlin.model.Category
import com.example.mealsappkotlin.model.Meal
import com.example.mealsappkotlin.network.RetrofitInstance
import com.google.gson.Gson

class MealRepository(context: Context) {

    private val api = RetrofitInstance.api
    private val prefs = context.getSharedPreferences("meal_cache", Context.MODE_PRIVATE)
    private val gson = Gson()


    suspend fun getRandomMeal(): Meal? =
        api.getRandomMeal().meals?.firstOrNull()

    suspend fun getCategories(): List<Category> =
        api.getCategories().categories ?: emptyList()

    suspend fun searchMeals(query: String): List<Meal> =
        api.searchMeals(query).meals ?: emptyList()

    suspend fun getMealsByCategory(category: String): List<Meal> =
        api.getMealsByCategory(category).meals ?: emptyList()

    suspend fun getMealById(id: String): Meal? {
        return try {
            val meal = api.getMealById(id).meals?.firstOrNull()
            if (meal != null) {
                prefs.edit().putString("meal_$id", gson.toJson(meal)).apply()
            }
            meal
        } catch (e: Exception) {
            val json = prefs.getString("meal_$id", null)
            if (json != null) gson.fromJson(json, Meal::class.java) else null
        }
    }
}
