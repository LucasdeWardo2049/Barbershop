package com.pdm.barbershop.ui.feature.barber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.repository.AppointmentsRepository
import com.pdm.barbershop.domain.model.Appointment
import com.pdm.barbershop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BarberDashboardUiState(
    val barberName: String = "Barbeiro",
    val upcomingAppointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class BarberDashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appointmentsRepository: AppointmentsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberDashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Carregar usuário (Barbeiro)
                val user = userRepository.currentUser.value
                if (user == null) {
                    userRepository.fetchUser()
                }
                val currentUser = userRepository.currentUser.value
                
                // 2. Carregar agendamentos do barbeiro
                val appointments = appointmentsRepository.listMyAppointments()
                
                _uiState.update { 
                    it.copy(
                        barberName = currentUser?.name ?: "Barbeiro",
                        upcomingAppointments = appointments,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // Tratar erro
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun refresh() {
        loadData()
    }
}
