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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.DayOfWeek

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

class ScheduleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
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

            _uiState.update {
                it.copy(services = services, barbers = barbers, availableDates = dates)
            }
        }
    }

    fun onServiceSelected(service: Service?) {
        _uiState.update { it.copy(selectedService = service) }
        checkIfButtonShouldBeEnabled()
    }

    fun onBarberSelected(barber: Barber?) {
        _uiState.update { it.copy(selectedBarber = barber) }
        checkIfButtonShouldBeEnabled()
    }

    fun onDateSelected(date: LocalDate?) {
        _uiState.update { it.copy(selectedDate = date, isLoadingTimeSlots = true, selectedTime = null) }
        if (date != null) {
            viewModelScope.launch {
                delay(500) // Simula chamada de rede

                val mockTimes = if (date.dayOfWeek == DayOfWeek.SUNDAY) {
                    emptyList()
                } else {
                    listOf("09:00", "09:30", "10:00", "11:30", "14:00", "14:30", "15:00", "16:30", "17:00")
                }

                _uiState.update { it.copy(availableTimeSlots = mockTimes, isLoadingTimeSlots = false) }
                checkIfButtonShouldBeEnabled()
            }
        }
    }

    fun onTimeSelected(time: String) {
        _uiState.update { it.copy(selectedTime = time) }
        checkIfButtonShouldBeEnabled()
    }

    private fun checkIfButtonShouldBeEnabled() {
        val currentState = _uiState.value
        val isEnabled = currentState.selectedService != null &&
                currentState.selectedBarber != null &&
                currentState.selectedDate != null &&
                currentState.selectedTime != null
        _uiState.update { it.copy(isConfirmationButtonEnabled = isEnabled) }
    }
}
