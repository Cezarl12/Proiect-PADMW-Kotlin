package com.example.mealsappkotlin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealsappkotlin.model.Category
import com.example.mealsappkotlin.model.Meal
import com.example.mealsappkotlin.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {

    private val _randomMeal = MutableStateFlow<Meal?>(null)
    val randomMeal: StateFlow<Meal?> = _randomMeal

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals

    private val _selectedMeal = MutableStateFlow<Meal?>(null)
    val selectedMeal: StateFlow<Meal?> = _selectedMeal

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadRandomMeal() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = RetrofitInstance.api.getRandomMeal()
                _randomMeal.value = result.meals?.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.api.getCategories()
                _categories.value = result.categories ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchMeals(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = RetrofitInstance.api.searchMeals(query)
                _meals.value = result.meals ?: emptyList()
            } catch (e: Exception) {
                _meals.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMealsByCategory(category: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = RetrofitInstance.api.getMealsByCategory(category)
                _meals.value = result.meals ?: emptyList()
            } catch (e: Exception) {
                _meals.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMealById(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = RetrofitInstance.api.getMealById(id)
                _selectedMeal.value = result.meals?.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}