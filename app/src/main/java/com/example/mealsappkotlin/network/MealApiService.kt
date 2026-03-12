package com.example.mealsappkotlin.network

import com.example.mealsappkotlin.model.Category
import com.example.mealsappkotlin.model.Meal
import retrofit2.http.GET
import retrofit2.http.Query
data class MealResponse(val meals: List<Meal>?)
data class CategoryResponse(val categories: List<Category>?)

interface MealApiService {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse

    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealResponse

    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse

    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealResponse
}