package com.pdm.barbershop.ui.feature.barber

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class BarberScheduleUiState(
    val appointments: List<Appointment> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(ZoneId.of("America/Manaus")),
    val isLoading: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class BarberScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberScheduleUiState())
    val uiState = _uiState.asStateFlow()

    // Use o mesmo ZoneId definido em DateTimeUtils ou hardcode para Manaus por consistência
    private val zoneId = ZoneId.of("America/Manaus")

    init {
        loadScheduleForDate(_uiState.value.selectedDate)
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadScheduleForDate(date)
    }

    private fun loadScheduleForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val allAppointments = scheduleRepository.getAppointments()
                
                // Filtra agendamentos para a data selecionada
                // Supondo que startTime venha como ISO (ex: 2025-11-30T14:00:00) ou ISO com offset
                val dateString = date.toString() // YYYY-MM-DD
                
                val filteredAppointments = allAppointments.filter { 
                    // Verifica se a data do agendamento corresponde à data selecionada (no fuso correto)
                    try {
                        // Usando DateTimeUtils para extrair a data do ISO se necessário
                        // Se o startTime for simples YYYY-MM-DD... funciona com startsWith
                        // Se for UTC, precisamos converter para o fuso local antes de comparar o dia
                        DateTimeUtils.dateLabelFromIso(it.startTime, zoneId) == 
                            String.format("%02d/%02d", date.dayOfMonth, date.monthValue)
                    } catch (e: Exception) {
                         it.startTime.startsWith(dateString)
                    }
                }.sortedBy { it.startTime }

                _uiState.update { 
                    it.copy(
                        appointments = filteredAppointments,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun refresh() {
        loadScheduleForDate(_uiState.value.selectedDate)
    }
}
