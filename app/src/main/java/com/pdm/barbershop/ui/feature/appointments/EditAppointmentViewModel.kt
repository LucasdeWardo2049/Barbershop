package com.pdm.barbershop.ui.feature.appointments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.repository.ScheduleRepository
import com.pdm.barbershop.domain.model.Barber
import com.pdm.barbershop.domain.model.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class EditAppointmentUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val updateSuccess: Boolean = false,
    val services: List<Service> = emptyList(),
    val barbers: List<Barber> = emptyList(),
    val availableDates: List<LocalDate> = emptyList(),
    val availability: List<String> = emptyList(),
    
    // Estado da Edição
    val selectedService: Service? = null,
    val selectedBarber: Barber? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: String? = null,
    val originalAppointmentId: String? = null
) {
    val canSave: Boolean get() = selectedService != null && selectedBarber != null && selectedDate != null && selectedTime != null
}

@HiltViewModel
class EditAppointmentViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditAppointmentUiState())
    val uiState = _uiState.asStateFlow()
    
    private val appointmentId: String? = savedStateHandle["appointmentId"]

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Carregar dados auxiliares (serviços, barbeiros)
                val services = scheduleRepository.loadServices()
                val barbers = scheduleRepository.loadBarbers()
                val dates = List(14) { LocalDate.now().plusDays(it.toLong()) }

                // Simular carregamento do agendamento existente pelo ID
                // Em um app real, chamaria repository.getAppointmentById(appointmentId)
                delay(500) // Simulação de network
                
                // Dados mockados do agendamento atual para preencher a tela
                val currentService = services.firstOrNull()
                val currentBarber = barbers.firstOrNull()
                val currentDate = LocalDate.now().plusDays(1)
                val currentTime = "2023-11-17T18:25:00" // Formato ISO simulado

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        services = services,
                        barbers = barbers,
                        availableDates = dates,
                        selectedService = currentService,
                        selectedBarber = currentBarber,
                        selectedDate = currentDate,
                        // selectedTime = currentTime, // Poderia pré-selecionar se a lista de horários estivesse carregada
                        originalAppointmentId = appointmentId
                    ) 
                }
                
                // Carregar horários para a data/barbeiro pré-selecionados
                if (currentBarber != null && currentService != null) {
                    loadAvailability(currentBarber, currentService, currentDate)
                }

            } catch (e: Exception) {
                // Tratar erro
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectService(service: Service) {
        _uiState.update { it.copy(selectedService = service, selectedTime = null) }
        refreshAvailability()
    }

    fun selectBarber(barber: Barber) {
        _uiState.update { it.copy(selectedBarber = barber, selectedTime = null) }
        refreshAvailability()
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, selectedTime = null) }
        refreshAvailability()
    }

    fun selectTime(time: String) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    private fun refreshAvailability() {
        val state = _uiState.value
        if (state.selectedBarber != null && state.selectedService != null && state.selectedDate != null) {
            loadAvailability(state.selectedBarber, state.selectedService, state.selectedDate)
        }
    }

    private fun loadAvailability(barber: Barber, service: Service, date: LocalDate) {
        viewModelScope.launch {
            try {
                val slots = scheduleRepository.loadAvailability(barber.id.toLong(), service.id.toLong(), date.toString())
                _uiState.update { it.copy(availability = slots) }
            } catch (e: Exception) {
                // Erro silencioso ou mensagem na UI
            }
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            delay(1500) // Simular salvamento no backend
            // Aqui chamaria repository.updateAppointment(...)
            _uiState.update { it.copy(isSaving = false, updateSuccess = true) }
        }
    }
}
