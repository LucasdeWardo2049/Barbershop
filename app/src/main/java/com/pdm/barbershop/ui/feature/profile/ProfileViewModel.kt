package com.pdm.barbershop.ui.feature.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data class para representar o estado da UI de perfil
data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val profileImageUri: Uri? = null, // Novo: para guardar a URI da imagem
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(
                userName = "Eduardo",
                userEmail = "eduardo.dev@example.com",
                isLoading = false
            )
        }
    }

    // Novo: Função para atualizar a URI da imagem no estado
    fun onProfileImageChanged(uri: Uri?) {
        _uiState.value = _uiState.value.copy(profileImageUri = uri)
        // TODO: Futuramente, aqui entrará a lógica para fazer o upload da imagem para o servidor
    }

    fun logout() {
        // Implementar a lógica de logout
    }
}