package com.pdm.barbershop.ui

import androidx.lifecycle.ViewModel
import com.pdm.barbershop.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val userRole: UserRole? = null
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun onLoginSuccess(role: UserRole) {
        _uiState.update { it.copy(userRole = role) }
    }

    fun onLogout() {
        _uiState.update { it.copy(userRole = null) }
    }
}
