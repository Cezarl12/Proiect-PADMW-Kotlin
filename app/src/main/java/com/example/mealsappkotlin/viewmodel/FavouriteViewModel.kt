package com.example.mealsappkotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealsappkotlin.model.Meal
import com.example.mealsappkotlin.repository.AuthService
import com.example.mealsappkotlin.repository.FavouriteService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pentru gestionarea rețetelor favorite.
 *
 * Folosește AndroidViewModel → nu mai e nevoie de FavouriteViewModelFactory custom.
 * viewModel<FavouriteViewModel>() funcționează direct în orice Composable.
 */
class FavouriteViewModel(application: Application) : AndroidViewModel(application) {

    private val authService = AuthService()
    private val favouriteService = FavouriteService(authService, application)

    private val _favourites = MutableStateFlow<List<Meal>>(emptyList())
    val favourites: StateFlow<List<Meal>> = _favourites

    private val _favouriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favouriteIds: StateFlow<Set<String>> = _favouriteIds

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadFavourites() {
        viewModelScope.launch {
            _isLoading.value = true
            _favourites.value = favouriteService.getAll()
            _favouriteIds.value = _favourites.value.map { it.idMeal }.toSet()
            _isLoading.value = false
        }
    }

    fun toggle(meal: Meal) {
        viewModelScope.launch {
            favouriteService.toggle(meal)
            loadFavourites()
        }
    }

    fun isFavourite(mealId: String): Boolean =
        _favouriteIds.value.contains(mealId)
}
