package com.pdm.barbershop.ui.feature.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.remote.RegisterRequest
import com.pdm.barbershop.domain.model.UserRole
import com.pdm.barbershop.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<RegisterEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onRoleChange(isBarber: Boolean) {
        val role = if (isBarber) UserRole.BARBER else UserRole.CLIENT
        _uiState.update { it.copy(role = role) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            val state = uiState.value
            if (state.password != state.confirmPassword) {
                _eventChannel.send(RegisterEvent.ShowError("As senhas não coincidem"))
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            try {
                // No backend, o role pode ser string "client" ou "barber" (minusculo)
                val request = RegisterRequest(
                    name = state.name.trim(),
                    email = state.email.trim(),
                    password = state.password.trim(),
                    phone = state.phone.trim(),
                    role = state.role.name.lowercase() // Envia "client" ou "barber"
                )

                authRepository.register(request)
                _eventChannel.send(RegisterEvent.NavigateToLogin)

            } catch (e: Exception) {
                _eventChannel.send(RegisterEvent.ShowError(e.message ?: "An unknown error occurred"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CLIENT, // Default é Cliente
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false
)