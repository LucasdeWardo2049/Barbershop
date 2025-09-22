package com.pdm.barbershop.ui.feature.appointments

import androidx.lifecycle.ViewModel
import com.pdm.barbershop.domain.model.Appointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppointmentsUiState(
    val appointments: List<Appointment> = emptyList()
)

class AppointmentsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchAppointments()
    }

    private fun fetchAppointments() {
        // Dados Mockados
        _uiState.value = AppointmentsUiState(
            appointments = listOf(
                Appointment("1", "25/09/2025", "10:00", "Corte Degradê", "Renato Lima", "Confirmado"),
                Appointment("2", "18/09/2025", "15:30", "Barba Terapia", "Gabriel", "Concluído"),
                Appointment("3", "12/09/2025", "11:00", "Corte e Barba", "Renato Lima", "Concluído"),
                Appointment("4", "05/09/2025", "09:00", "Pintura Capilar", "André Guedes", "Concluído")
            )
        )
    }
}