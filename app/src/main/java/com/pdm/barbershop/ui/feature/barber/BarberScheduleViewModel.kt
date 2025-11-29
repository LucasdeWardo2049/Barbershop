package com.pdm.barbershop.ui.feature.barber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.repository.ScheduleRepository
import com.pdm.barbershop.domain.model.Appointment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class BarberScheduleUiState(
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class BarberScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberScheduleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTodaySchedule()
    }

    private fun loadTodaySchedule() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val allAppointments = scheduleRepository.getAppointments()
                // Filtra apenas agendamentos de hoje
                val today = LocalDate.now().toString()
                val todayAppointments = allAppointments.filter { 
                    it.startTime.startsWith(today) 
                }

                _uiState.update { 
                    it.copy(
                        appointments = todayAppointments,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun refresh() {
        loadTodaySchedule()
    }
}
