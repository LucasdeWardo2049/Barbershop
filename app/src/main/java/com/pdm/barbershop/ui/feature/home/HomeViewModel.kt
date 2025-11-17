package com.pdm.barbershop.ui.feature.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut // Para o ícone de exemplo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.repository.AppointmentsRepository
import com.pdm.barbershop.data.session.SessionManager
import com.pdm.barbershop.domain.model.Appointment
import com.pdm.barbershop.domain.model.Service
import com.pdm.barbershop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado da UI para a HomeScreen
data class HomeUiState(
    val userName: String = "",
    val nextAppointment: Appointment? = null,
    val lastServiceForRebooking: Service? = null,
    val isLoading: Boolean = false,
    val error: String? = null, // Para futuras mensagens de erro
    val sessionExpired: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appointmentsRepository: AppointmentsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeUser()
        observeSession()
        loadRemainingData()
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                _uiState.update { it.copy(userName = user?.name ?: "") }
                if (user != null) {
                    fetchAppointmentsForHome(user.userId)
                }
            }
        }
    }

    private fun observeSession() {
        viewModelScope.launch {
            sessionManager.sessionExpired.collectLatest {
                userRepository.clearUser()
                _uiState.update { it.copy(sessionExpired = true, error = "Sessão expirada. Faça login novamente.") }
            }
        }
    }

    fun consumeSessionExpired() { _uiState.update { it.copy(sessionExpired = false) } }

    @Suppress("NewApi")
    private fun fetchAppointmentsForHome(userId: String) {
        viewModelScope.launch {
            try {
                val list = appointmentsRepository.listMyAppointments()
                val next = list.firstOrNull()
                val lastService = list.lastOrNull()?.let { appt ->
                    Service(
                        id = appt.id,
                        name = appt.serviceName,
                        price = 0.0,
                        durationInMinutes = 30,
                        icon = Icons.Filled.ContentCut
                    )
                }
                _uiState.update { it.copy(nextAppointment = next, lastServiceForRebooking = lastService, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun loadRemainingData() {
        _uiState.update { it.copy(isLoading = true) }
    }
}
