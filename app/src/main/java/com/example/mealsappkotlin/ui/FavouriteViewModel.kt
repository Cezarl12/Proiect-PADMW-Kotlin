package com.example.mealsappkotlin.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealsappkotlin.model.Meal
import com.example.mealsappkotlin.repository.AuthService
import com.example.mealsappkotlin.repository.FavouriteService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel(context: Context) : ViewModel() {

    private val authService = AuthService()
    private val favouriteService = FavouriteService(authService, context)

    private val _favourites = MutableStateFlow<List<Meal>>(emptyList())
    val favourites: StateFlow<List<Meal>> = _favourites

    private val _favouriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favouriteIds: StateFlow<Set<String>> = _favouriteIds

    fun loadFavourites() {
        viewModelScope.launch {
            _favourites.value = favouriteService.getAll()
            _favouriteIds.value = _favourites.value.map { it.idMeal }.toSet()
        }
    }

    fun toggle(meal: Meal) {
        viewModelScope.launch {
            favouriteService.toggle(meal)
            loadFavourites()
        }
    }

    fun isFavourite(mealId: String): Boolean {
        return _favouriteIds.value.contains(mealId)
    }
}