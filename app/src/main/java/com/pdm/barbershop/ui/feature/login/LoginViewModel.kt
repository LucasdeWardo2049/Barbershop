package com.pdm.barbershop.ui.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.remote.AuthRequest
import com.pdm.barbershop.domain.model.UserRole
import com.pdm.barbershop.domain.repository.AuthRepository
import com.pdm.barbershop.domain.repository.TokenRepository
import com.pdm.barbershop.domain.repository.UserRepository
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
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
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
                tokenRepository.saveToken(authResponse.token)

                // Busca e armazena os dados do usuário
                userRepository.fetchUser()

                // Obter role do usuário logado
                val user = userRepository.currentUser.value
                val userRole = when (user?.role?.uppercase()) {
                    "ADMIN" -> UserRole.ADMIN
                    "BARBER" -> UserRole.BARBER
                    "CLIENT" -> UserRole.CLIENT
                    else -> UserRole.CLIENT // fallback
                }

                _eventChannel.send(LoginEvent.NavigateTo(userRole))

            } catch (e: HttpException) {
                _eventChannel.send(LoginEvent.ShowError("Email ou senha inválidos"))
            } catch (e: Exception) {
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
