package com.pdm.barbershop.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)

class EditProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        // Simula o carregamento dos dados atuais do usuário
        _uiState.value = EditProfileUiState(
            name = "Eduardo",
            email = "eduardo.dev@example.com",
            phone = "(99) 99999-9999"
        )
    }

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onPhoneChange(newPhone: String) {
        _uiState.value = _uiState.value.copy(phone = newPhone)
    }

    fun saveChanges() {
        viewModelScope.launch {
            // TODO: Implementar a lógica para salvar os dados no backend/banco de dados
            println("Salvando dados: ${_uiState.value}")
        }
    }
}
