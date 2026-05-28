package com.example.mealsappkotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealsappkotlin.model.Category
import com.example.mealsappkotlin.model.Meal
import com.example.mealsappkotlin.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MealViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MealRepository(application)

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


    private val _mealDetails = MutableStateFlow<Map<String, Meal>>(emptyMap())
    val mealDetails: StateFlow<Map<String, Meal>> = _mealDetails

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    fun loadRandomMeal() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _randomMeal.value = repository.getRandomMeal()
            } catch (e: Exception) {
                _error.value = "Nu s-a putut încărca rețeta zilei"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _error.value = null
            try {
                _categories.value = repository.getCategories()
            } catch (e: Exception) {
                _error.value = "Nu s-au putut încărca categoriile"
            }
        }
    }

    fun searchMeals(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _mealDetails.value = emptyMap()
            try {
                _meals.value = repository.searchMeals(query)
            } catch (e: Exception) {
                _meals.value = emptyList()
                _error.value = "Căutarea a eșuat"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMealsByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _mealDetails.value = emptyMap()
            try {
                _meals.value = repository.getMealsByCategory(category)
            } catch (e: Exception) {
                _meals.value = emptyList()
                _error.value = "Nu s-au putut încărca rețetele"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMealById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _selectedMeal.value = repository.getMealById(id)
            } catch (e: Exception) {
                _error.value = "Nu s-a putut încărca rețeta"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun loadMealDetailsForResults(mealIds: List<String>) {
        viewModelScope.launch {
            val details = mutableMapOf<String, Meal>()
            mealIds.forEach { id ->
                try {
                    repository.getMealById(id)?.let { details[id] = it }
                } catch (e: Exception) { /* silently skip */ }
            }
            _mealDetails.value = details
        }
    }
}
