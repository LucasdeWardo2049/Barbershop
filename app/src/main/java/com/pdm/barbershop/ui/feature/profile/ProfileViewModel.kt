package com.pdm.barbershop.ui.feature.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val profileImageUri: Uri? = null,
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

    fun onProfileImageChanged(uri: Uri?) {
        _uiState.value = _uiState.value.copy(profileImageUri = uri)
        // TODO: Implementar upload da imagem para o servidor futuramente
    }

    fun logout() {
        viewModelScope.launch {
            // TODO: Limpar sessão, tokens, etc.
            _uiState.value = ProfileUiState() // Reseta estado
        }
    }
}
