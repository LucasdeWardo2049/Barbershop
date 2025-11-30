package com.pdm.barbershop.ui.feature.barber

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    
    // Define o fuso horário de Manaus
    private val zoneId = ZoneId.of("America/Manaus")

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, successMessage = null, errorMessage = null) }
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

        // Validação simples
        if (state.endTime.isBefore(state.startTime)) {
            _uiState.update { it.copy(errorMessage = "Horário final deve ser após o inicial.") }
            return
        }

        // Obtém a data e hora atual no fuso horário correto (Manaus)
        val nowInZone = LocalTime.now(zoneId)
        val todayInZone = LocalDate.now(zoneId)

        // Validação de horário passado considerando o fuso horário
        if (date.isEqual(todayInZone) && state.startTime.isBefore(nowInZone)) {
            _uiState.update { it.copy(errorMessage = "O horário de início já passou.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                // Garante que o usuário esteja carregado
                var user = userRepository.currentUser.value
                if (user == null) {
                    userRepository.fetchUser()
                    user = userRepository.currentUser.value
                }
                
                // Aqui pegamos o barberId correto vindo do backend (que é retornado no /me)
                // Se o barberId for nulo, significa que o usuário não tem perfil de barbeiro associado corretamente
                val barberId = user?.barberId ?: throw Exception("Perfil de barbeiro não encontrado para este usuário.")

                // Converte para DayOfWeek (1=Monday, 7=Sunday)
                val dayOfWeek = date.dayOfWeek.value 

                // Formata para HH:mm:ss
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
                        successMessage = "Horário adicionado com sucesso para ${date.dayOfWeek.name}!"
                    ) 
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Erro ao salvar horário.") }
            }
        }
    }
}
