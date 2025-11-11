package com.pdm.barbershop.ui.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut // Para o ícone de exemplo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.domain.model.Appointment
import com.pdm.barbershop.domain.model.Service
import com.pdm.barbershop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estado da UI para a HomeScreen
data class HomeUiState(
    val userName: String = "",
    val nextAppointment: Appointment? = null,
    val lastServiceForRebooking: Service? = null,
    val isLoading: Boolean = false,
    val error: String? = null // Para futuras mensagens de erro
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeUser()
        loadRemainingData()
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                _uiState.update { it.copy(userName = user?.name ?: "") }
            }
        }
    }

    private fun loadRemainingData() {
        _uiState.update { it.copy(isLoading = true) }

        // Dados de exemplo para o resto da tela
        val exampleNextAppointment = Appointment(
            id = "appt123",
            date = "24/09", // Formato DD/MM
            time = "15:00",
            serviceName = "Corte + Barba", // Pode ser uma combinação ou o nome principal
            barberName = "Fernando Silva",
            status = "Confirmado"
        )

        val exampleLastService = Service(
            id = "serv456",
            name = "Corte Degradê",
            price = 50.0,
            durationInMinutes = 45,
            icon = Icons.Filled.ContentCut // Ícone de exemplo
        )

        _uiState.update {
            it.copy(
                nextAppointment = exampleNextAppointment,
                lastServiceForRebooking = exampleLastService,
                isLoading = false
            )
        }
    }
}
