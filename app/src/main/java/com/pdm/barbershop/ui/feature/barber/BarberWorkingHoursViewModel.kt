package com.pdm.barbershop.ui.feature.barber

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.remote.dto.WorkingHourResponse
import com.pdm.barbershop.data.remote.dto.WorkingHoursRequest
import com.pdm.barbershop.data.repository.ScheduleRepository
import com.pdm.barbershop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

data class BarberWorkingHoursUiState(
    val selectedDate: LocalDate? = null,
    val startTime: LocalTime = LocalTime.of(9, 0),
    val endTime: LocalTime = LocalTime.of(18, 0),
    val workingHoursList: List<WorkingHourResponse> = emptyList(),
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class BarberWorkingHoursViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberWorkingHoursUiState())
    val uiState = _uiState.asStateFlow()
    
    private val zoneId = ZoneId.of("America/Manaus")

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, successMessage = null, errorMessage = null) }
        loadWorkingHoursForDay(date.dayOfWeek.value)
    }

    private fun loadWorkingHoursForDay(dayOfWeek: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = userRepository.currentUser.value ?: run {
                    userRepository.fetchUser()
                    userRepository.currentUser.value
                }
                val barberId = user?.barberId ?: throw Exception("Perfil de barbeiro não encontrado.")

                val hours = scheduleRepository.searchWorkingHours(barberId, dayOfWeek)
                _uiState.update { it.copy(workingHoursList = hours, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar horários: ${e.message}") }
            }
        }
    }

    fun setStartTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(startTime = LocalTime.of(hour, minute)) }
    }

    fun setEndTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(endTime = LocalTime.of(hour, minute)) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    fun saveWorkingHours() {
        val state = uiState.value
        val date = state.selectedDate ?: run {
            _uiState.update { it.copy(errorMessage = "Selecione uma data.") }
            return
        }

        if (state.endTime.isBefore(state.startTime)) {
            _uiState.update { it.copy(errorMessage = "Horário final deve ser após o inicial.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                val user = userRepository.currentUser.value ?: run {
                    userRepository.fetchUser()
                    userRepository.currentUser.value
                }
                val barberId = user?.barberId ?: throw Exception("Perfil de barbeiro não encontrado.")

                val dayOfWeek = date.dayOfWeek.value 
                val startFormatted = String.format("%02d:%02d:00", state.startTime.hour, state.startTime.minute)
                val endFormatted = String.format("%02d:%02d:00", state.endTime.hour, state.endTime.minute)

                scheduleRepository.addWorkingHours(
                    barberId = barberId,
                    dayOfWeek = dayOfWeek,
                    startTime = startFormatted,
                    endTime = endFormatted
                )

                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        successMessage = "Horário adicionado com sucesso!"
                    ) 
                }
                loadWorkingHoursForDay(dayOfWeek)

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Erro ao salvar horário.") }
            }
        }
    }

    fun deleteWorkingHour(workingHourId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                scheduleRepository.deleteWorkingHours(workingHourId)
                _uiState.update { it.copy(successMessage = "Horário removido com sucesso!", isLoading = false) }
                _uiState.value.selectedDate?.let { loadWorkingHoursForDay(it.dayOfWeek.value) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao remover: ${e.message}", isLoading = false) }
            }
        }
    }

    fun updateWorkingHour(workingHourId: Long, newStart: LocalTime, newEnd: LocalTime) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = userRepository.currentUser.value!!
                val barberId = user.barberId!!
                val dayOfWeek = _uiState.value.selectedDate!!.dayOfWeek.value

                val startFormatted = String.format("%02d:%02d:00", newStart.hour, newStart.minute)
                val endFormatted = String.format("%02d:%02d:00", newEnd.hour, newEnd.minute)

                val request = WorkingHoursRequest(
                    barberId = barberId,
                    dayOfWeek = dayOfWeek,
                    startTime = startFormatted,
                    endTime = endFormatted
                )

                scheduleRepository.updateWorkingHours(workingHourId, request)
                
                _uiState.update { it.copy(successMessage = "Horário atualizado!", isLoading = false) }
                loadWorkingHoursForDay(dayOfWeek)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao atualizar: ${e.message}", isLoading = false) }
            }
        }
    }
}
