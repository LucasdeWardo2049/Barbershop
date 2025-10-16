package com.pdm.barbershop.ui.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.domain.model.UserRole
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<LoginEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simula chamada de rede

            val email = uiState.value.email
            val password = uiState.value.password

            val role = when {
                email == "cliente@cliente.com" && password == "cliente" -> UserRole.CLIENT
                email == "barbeiro@barbeiro.com" && password == "barbeiro" -> UserRole.BARBER
                email == "admin@admin.com" && password == "admin" -> UserRole.ADMIN
                else -> null
            }

            if (role != null) {
                _eventChannel.send(LoginEvent.NavigateTo(role))
            } else {
                _eventChannel.send(LoginEvent.ShowError("Email ou senha inválidos"))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)
