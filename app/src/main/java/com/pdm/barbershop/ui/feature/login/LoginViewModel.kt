package com.pdm.barbershop.ui.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.remote.AuthRequest
import com.pdm.barbershop.domain.model.UserRole
import com.pdm.barbershop.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val email = uiState.value.email.trim()
                val password = uiState.value.password.trim()
                val authRequest = AuthRequest(email, password)

                val authResponse = authRepository.login(authRequest)

                // TODO: Save the token securely (e.g., EncryptedSharedPreferences)
                // TODO: Decode the token to get the user role
                _eventChannel.send(LoginEvent.NavigateTo(UserRole.CLIENT))

            } catch (e: HttpException) {
                // Erro de HTTP, como 401 (Não autorizado) ou 404 (Não encontrado)
                _eventChannel.send(LoginEvent.ShowError("Email ou senha inválidos"))
            } catch (e: Exception) {
                // Erro genérico, como falta de conexão com a internet
                _eventChannel.send(LoginEvent.ShowError("Não foi possível conectar ao servidor"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false
)
