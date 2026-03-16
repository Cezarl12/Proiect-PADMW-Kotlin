package com.example.mealsappkotlin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealsappkotlin.model.User
import com.example.mealsappkotlin.repository.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authService = AuthService()

    private val _isLoggedIn = MutableStateFlow(authService.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val user = authService.login(email, password)
            if (user != null) {
                _isLoggedIn.value = true
                onSuccess()
            } else {
                _error.value = "Email sau parolă greșită"
            }
            _isLoading.value = false
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val success = authService.register(name, email, password)
            if (success) {
                val user = authService.login(email, password)
                if (user != null) {
                    _isLoggedIn.value = true
                    onSuccess()
                }
            } else {
                _error.value = "Înregistrare eșuată"
            }
            _isLoading.value = false
        }
    }

    fun logout(onSuccess: () -> Unit) {
        authService.logout()
        _isLoggedIn.value = false
        onSuccess()
    }

    fun getCurrentUserId() = authService.getCurrentUserId()
}