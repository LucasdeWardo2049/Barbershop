package com.pdm.barbershop.ui.feature.schedule

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.domain.model.Barber
import com.pdm.barbershop.domain.model.Service
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.DayOfWeek

// Conteúdo detalhado da tela de agendamento
data class ScheduleUiState(
    val services: List<Service> = emptyList(),
    val barbers: List<Barber> = emptyList(),
    val availableDates: List<LocalDate> = emptyList(),
    val availableTimeSlots: List<String> = emptyList(),
    val selectedService: Service? = null,
    val selectedBarber: Barber? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: String? = null,
    val isConfirmationButtonEnabled: Boolean = false,
    val isLoadingTimeSlots: Boolean = false
)

// Padrão Loading/Content/Error para a tela Schedule
sealed interface ScheduleUiRoot {
    data object Loading : ScheduleUiRoot
    data class Content(val data: ScheduleUiState) : ScheduleUiRoot
    data class Error(val message: String) : ScheduleUiRoot
}

class ScheduleViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ScheduleUiRoot> = MutableStateFlow(ScheduleUiRoot.Content(ScheduleUiState()))
    val uiState: StateFlow<ScheduleUiRoot> = _uiState

    // Stream conveniente apenas com o conteúdo (para telas que não precisam diferenciar Loading/Error)
    val content: StateFlow<ScheduleUiState> = uiState
        .map { root -> (root as? ScheduleUiRoot.Content)?.data ?: ScheduleUiState() }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, ScheduleUiState())

    init {
        loadInitialData()
    }

    private fun updateContent(transform: (ScheduleUiState) -> ScheduleUiState) {
        val current = _uiState.value
        if (current is ScheduleUiRoot.Content) {
            _uiState.value = ScheduleUiRoot.Content(transform(current.data))
        }
    }

    private fun readContent(): ScheduleUiState? = (_uiState.value as? ScheduleUiRoot.Content)?.data

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                val services = listOf(
                    Service("1", "Corte", 50.0, 30, Icons.Default.ContentCut),
                    Service("2", "Barba", 40.0, 30, Icons.Default.Face),
                    Service("3", "Sobrancelha", 20.0, 15, Icons.Default.Person)
                )
                val barbers = listOf(
                    Barber("1", "Renato Lima", 4.5, "https://i.pravatar.cc/150?img=1"),
                    Barber("2", "Gabriel Becil", 4.7, "https://i.pravatar.cc/150?img=3"),
                    Barber("3", "André Guedes", 4.2, "https://i.pravatar.cc/150?img=5")
                )
                val dates = List(7) { LocalDate.now().plusDays(it.toLong()) }

                updateContent { it.copy(services = services, barbers = barbers, availableDates = dates) }
            } catch (t: Throwable) {
                _uiState.value = ScheduleUiRoot.Error(t.message ?: "Erro ao carregar dados do agendamento")
            }
        }
    }

    fun onServiceSelected(service: Service?) {
        updateContent { it.copy(selectedService = service) }
        checkIfButtonShouldBeEnabled()
    }

    fun onBarberSelected(barber: Barber?) {
        updateContent { it.copy(selectedBarber = barber) }
        checkIfButtonShouldBeEnabled()
    }

    fun onDateSelected(date: LocalDate?) {
        updateContent { it.copy(selectedDate = date, isLoadingTimeSlots = true, selectedTime = null) }
        if (date != null) {
            viewModelScope.launch {
                delay(500) // Simula chamada de rede

                val mockTimes = if (date.dayOfWeek == DayOfWeek.SUNDAY) {
                    emptyList()
                } else {
                    listOf("09:00", "09:30", "10:00", "11:30", "14:00", "14:30", "15:00", "16:30", "17:00")
                }

                updateContent { it.copy(availableTimeSlots = mockTimes, isLoadingTimeSlots = false) }
                checkIfButtonShouldBeEnabled()
            }
        }
    }

    fun onTimeSelected(time: String) {
        updateContent { it.copy(selectedTime = time) }
        checkIfButtonShouldBeEnabled()
    }

    private fun checkIfButtonShouldBeEnabled() {
        val currentState = readContent() ?: return
        val isEnabled = currentState.selectedService != null &&
                currentState.selectedBarber != null &&
                currentState.selectedDate != null &&
                currentState.selectedTime != null
        updateContent { it.copy(isConfirmationButtonEnabled = isEnabled) }
    }
}
