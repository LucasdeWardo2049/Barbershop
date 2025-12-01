package com.pdm.barbershop.ui.feature.barber

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.repository.AppointmentsRepository
import com.pdm.barbershop.data.repository.ScheduleRepository
import com.pdm.barbershop.domain.model.Appointment
import com.pdm.barbershop.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class BarberAppointmentFilter {
    ALL,
    SCHEDULED,
    CANCELLED,
    COMPLETED
}

data class BarberScheduleUiState(
    val appointments: List<Appointment> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(ZoneId.of("America/Manaus")),
    val isLoading: Boolean = false,
    val currentFilter: BarberAppointmentFilter = BarberAppointmentFilter.ALL,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    
    // Reschedule State
    val showRescheduleDialog: Boolean = false,
    val rescheduleAppointment: Appointment? = null,
    val availableSlots: List<String> = emptyList(),
    val selectedSlot: String? = null,
    val loadingSlots: Boolean = false
) {
    val filteredAppointments: List<Appointment>
        get() = when (currentFilter) {
            BarberAppointmentFilter.ALL -> appointments
            BarberAppointmentFilter.SCHEDULED -> appointments.filter {
                it.status.uppercase() in listOf("SCHEDULED", "CONFIRMED", "AGENDADO")
            }
            BarberAppointmentFilter.CANCELLED -> appointments.filter {
                it.status.uppercase() in listOf("CANCELLED", "CANCELADO")
            }
            BarberAppointmentFilter.COMPLETED -> appointments.filter {
                it.status.uppercase() in listOf("COMPLETED", "CONCLUIDO")
            }
        }
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class BarberScheduleViewModel @Inject constructor(
    private val appointmentsRepository: AppointmentsRepository,
    private val scheduleRepository: ScheduleRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberScheduleUiState())
    val uiState = _uiState.asStateFlow()

    private val zoneId = ZoneId.of("America/Manaus")

    init {
        // Check for passed date from navigation
        val passedDate = savedStateHandle.get<String>("date")
        val dateToLoad = if (passedDate != null) {
            try {
                LocalDate.parse(passedDate)
            } catch (e: Exception) {
                LocalDate.now(zoneId)
            }
        } else {
            LocalDate.now(zoneId)
        }
        
        _uiState.update { it.copy(selectedDate = dateToLoad) }
        loadScheduleForDate(dateToLoad)
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadScheduleForDate(date)
    }

    fun setFilter(filter: BarberAppointmentFilter) {
        _uiState.update { it.copy(currentFilter = filter) }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    fun refresh() {
        loadScheduleForDate(_uiState.value.selectedDate)
    }

    private fun loadScheduleForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val allAppointments = appointmentsRepository.listMyAppointments()
                
                val dateString = date.toString()
                
                val dayAppointments = allAppointments.filter { appointment ->
                    try {
                        DateTimeUtils.dateLabelFromIso(appointment.startTime, zoneId) == 
                            String.format("%02d/%02d", date.dayOfMonth, date.monthValue)
                    } catch (e: Exception) {
                         appointment.startTime.startsWith(dateString)
                    }
                }.sortedBy { it.startTime }

                _uiState.update { 
                    it.copy(
                        appointments = dayAppointments,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar agenda: ${e.message}") }
            }
        }
    }

    // --- Lógica de Cancelamento ---
    fun cancelAppointment(appointmentId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            appointmentsRepository.cancelAppointment(appointmentId.toLong())
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Agendamento cancelado com sucesso.") }
                    refresh() 
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao cancelar: ${e.message}") }
                }
        }
    }

    // --- Lógica de Reagendamento ---
    fun openRescheduleDialog(appointment: Appointment) {
        _uiState.update { 
            it.copy(
                showRescheduleDialog = true, 
                rescheduleAppointment = appointment,
                selectedSlot = null,
                availableSlots = emptyList()
            ) 
        }
        loadAvailableSlots()
    }

    fun closeRescheduleDialog() {
        _uiState.update { it.copy(showRescheduleDialog = false, rescheduleAppointment = null) }
    }

    fun selectSlot(slot: String) {
        _uiState.update { it.copy(selectedSlot = slot) }
    }

    private fun loadAvailableSlots() {
        val state = _uiState.value
        val appointment = state.rescheduleAppointment ?: return
        val date = state.selectedDate 

        viewModelScope.launch {
            _uiState.update { it.copy(loadingSlots = true) }
            try {
                val slots = scheduleRepository.loadAvailability(
                    barberId = appointment.barberId.toLong(),
                    serviceId = appointment.serviceId.toLong(),
                    dateISO = date.toString()
                )
                
                val filtered = slots.filter { !DateTimeUtils.isIsoPast(it) }
                
                _uiState.update { it.copy(availableSlots = filtered, loadingSlots = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(loadingSlots = false, errorMessage = "Erro ao carregar horários: ${e.message}") }
            }
        }
    }

    fun confirmReschedule() {
        val state = _uiState.value
        val appointment = state.rescheduleAppointment ?: return
        val slot = state.selectedSlot ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            appointmentsRepository.rescheduleAppointment(
                appointmentId = appointment.appointmentId.toLong(),
                barberId = appointment.barberId.toLong(),
                serviceId = appointment.serviceId.toLong(),
                startTime = slot
            ).onSuccess {
                _uiState.update { it.copy(successMessage = "Agendamento reagendado com sucesso!", showRescheduleDialog = false) }
                refresh()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao reagendar: ${e.message}") }
            }
        }
    }
}
