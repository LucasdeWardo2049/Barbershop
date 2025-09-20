package com.pdm.barbershop.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data class para representar o estado da UI de perfil
data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        // Ao inicializar o ViewModel, busca os dados do usuário
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // --- Lógica para buscar dados do usuário (ex: de uma API ou banco de dados) ---
            // Por enquanto, usaremos dados de exemplo após um pequeno delay.
            kotlinx.coroutines.delay(1000)

            _uiState.value = _uiState.value.copy(
                userName = "Eduardo",
                userEmail = "eduardo.dev@example.com",
                isLoading = false
            )
        }
    }

    // Função para fazer logout (exemplo)
    fun logout() {
        // --- Implementar a lógica de logout aqui ---
        // Limpar tokens de autenticação, etc.
    }
}

