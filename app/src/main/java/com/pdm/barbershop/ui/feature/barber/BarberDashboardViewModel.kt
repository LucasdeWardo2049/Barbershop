package com.pdm.barbershop.ui.feature.barber

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

data class BarberDashboardUiState(
    val barberName: String = "Barbeiro",
    val upcomingAppointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
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
                // listMyAppointments() retorna agendamentos do barbeiro logado (backend filtra pelo token/contexto)
                val appointments = appointmentsRepository.listMyAppointments()
                
                // Ordena por data mais próxima e filtra apenas futuros ou hoje
                val now = OffsetDateTime.now(ZoneId.of("America/Manaus"))
                
                val sortedAppointments = appointments
                    .filter { 
                        try {
                            // Parse manual do ISO, já que DateTimeUtils.parseIso não existe
                            val apptTime = OffsetDateTime.parse(it.startTime)
                            apptTime.isAfter(now)
                        } catch (e: Exception) {
                            false
                        }
                    }
                    .sortedBy { it.startTime }
                
                _uiState.update { 
                    it.copy(
                        barberName = currentUser?.name ?: "Barbeiro",
                        upcomingAppointments = sortedAppointments,
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
