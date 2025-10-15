package com.pdm.barbershop.ui.feature.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Content(
        val userName: String = "",
        val userEmail: String = "",
        val profileImageUri: Uri? = null
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(1000)
                _uiState.value = ProfileUiState.Content(
                    userName = "Eduardo",
                    userEmail = "eduardo.dev@example.com",
                    profileImageUri = null
                )
            } catch (t: Throwable) {
                _uiState.value = ProfileUiState.Error(t.message ?: "Erro ao carregar perfil")
            }
        }
    }

    fun onProfileImageChanged(uri: Uri?) {
        val current = _uiState.value
        if (current is ProfileUiState.Content) {
            _uiState.value = current.copy(profileImageUri = uri)
        }
    }

    fun logout() {
        viewModelScope.launch {
            // TODO: Limpar sessão, tokens, etc.
            _uiState.value = ProfileUiState.Content() // Reseta estado visual para vazio
        }
    }
}
