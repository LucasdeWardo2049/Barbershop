package com.pdm.barbershop.ui.feature.appointments

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.pdm.barbershop.domain.model.Appointment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.pdm.barbershop.data.repository.AppointmentsRepository
import com.pdm.barbershop.data.repository.ScheduleRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.ZoneId

enum class AppointmentFilter {
    ALL,           // Todos
    SCHEDULED,     // Agendados (SCHEDULED, CONFIRMED)
    CANCELLED,     // Cancelados
    COMPLETED      // Concluídos
}

data class AppointmentsUiState(
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentFilter: AppointmentFilter = AppointmentFilter.ALL,
    // Reschedule dialog state
    val showRescheduleDialog: Boolean = false,
    val rescheduleAppointment: Appointment? = null,
    val availableDates: List<LocalDate> = emptyList(),
    val selectedDate: LocalDate? = null,
    val availableSlots: List<String> = emptyList(),
    val selectedSlot: String? = null,
    val loadingSlots: Boolean = false
) {
    val filteredAppointments: List<Appointment>
        get() = when (currentFilter) {
            AppointmentFilter.ALL -> appointments
            AppointmentFilter.SCHEDULED -> appointments.filter {
                it.status.uppercase() in listOf("SCHEDULED", "CONFIRMED", "AGENDADO")
            }
            AppointmentFilter.CANCELLED -> appointments.filter {
                it.status.uppercase() in listOf("CANCELLED", "CANCELADO")
            }
            AppointmentFilter.COMPLETED -> appointments.filter {
                it.status.uppercase() in listOf("COMPLETED", "CONCLUIDO")
            }
        }
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val appointmentsRepository: AppointmentsRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AppointmentsVM"
    }

    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState = _uiState.asStateFlow()

    private val zone = ZoneId.of("America/Manaus")

    init {
        Log.d(TAG, "🚀 ViewModel inicializado")
        fetchAppointments()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAppointments() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val list = appointmentsRepository.listMyAppointments()
                _uiState.update { it.copy(
                    appointments = list,
                    isLoading = false,
                    errorMessage = null
                ) }
            } catch (e: Exception) {
                val errorMsg = classifyError(e)
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                ) }
            }
        }
    }

    fun cancelAppointment(appointmentId: Int) {
        Log.d(TAG, "🗑️ Cancelando agendamento ID: $appointmentId")
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            try {
                appointmentsRepository.cancelAppointment(appointmentId.toLong())
                _uiState.update { it.copy(successMessage = "Agendamento cancelado com sucesso") }
                // Recarrega a lista após cancelar
                fetchAppointments()
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao cancelar: ${e.message}", e)
                val errorMsg = classifyError(e)
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            }
        }
    }

    fun setFilter(filter: AppointmentFilter) {
        _uiState.update { it.copy(currentFilter = filter) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Reschedule dialog methods
    fun openRescheduleDialog(appointment: Appointment) {
        Log.d(TAG, "📅 Abrindo diálogo de reagendamento para: ${appointment.appointmentId}")
        _uiState.update {
            it.copy(
                showRescheduleDialog = true,
                rescheduleAppointment = appointment,
                selectedDate = null,
                selectedSlot = null,
                availableDates = emptyList(),
                availableSlots = emptyList()
            )
        }
        loadAvailableDates()
    }

    fun closeRescheduleDialog() {
        _uiState.update {
            it.copy(
                showRescheduleDialog = false,
                rescheduleAppointment = null,
                selectedDate = null,
                selectedSlot = null,
                availableDates = emptyList(),
                availableSlots = emptyList()
            )
        }
    }

    private fun loadAvailableDates() {
        Log.d(TAG, "📅 Carregando datas disponíveis...")
        viewModelScope.launch {
            try {
                // Gerar próximos 30 dias como disponíveis
                val today = LocalDate.now(zone)
                val dates = (0..29).map { today.plusDays(it.toLong()) }
                _uiState.update { it.copy(availableDates = dates) }
                Log.d(TAG, "✅ ${dates.size} datas carregadas")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao carregar datas: ${e.message}", e)
            }
        }
    }

    fun selectDate(date: LocalDate) {
        Log.d(TAG, "📅 Data selecionada: $date")
        _uiState.update { it.copy(selectedDate = date, selectedSlot = null, loadingSlots = true) }
        loadAvailableSlots(date)
    }

    private fun loadAvailableSlots(date: LocalDate) {
        val appointment = _uiState.value.rescheduleAppointment ?: return
        Log.d(TAG, "⏰ Carregando horários para data: $date")

        viewModelScope.launch {
            try {
                val slots = scheduleRepository.getAvailability(
                    barberId = appointment.barberId.toLong(),
                    serviceId = appointment.serviceId.toLong(),
                    date = date
                )
                Log.d(TAG, "📅 API retornou ${slots.size} horários")

                _uiState.update { it.copy(availableSlots = slots, loadingSlots = false) }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao carregar horários: ${e.message}", e)
                _uiState.update { it.copy(errorMessage = classifyError(e), loadingSlots = false) }
            }
        }
    }

    fun selectSlot(slot: String) {
        Log.d(TAG, "⏰ Horário selecionado: $slot")
        _uiState.update { it.copy(selectedSlot = slot) }
    }

    fun confirmReschedule() {
        val state = _uiState.value
        val appointment = state.rescheduleAppointment ?: return
        val slot = state.selectedSlot ?: return

        Log.d(TAG, "✅ Confirmando reagendamento...")
        Log.d(TAG, "✅ Agendamento ID: ${appointment.appointmentId}")
        Log.d(TAG, "✅ Horário selecionado (ISO): $slot")

        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            try {
                appointmentsRepository.rescheduleAppointment(
                    appointmentId = appointment.appointmentId.toLong(),
                    barberId = appointment.barberId.toLong(),
                    serviceId = appointment.serviceId.toLong(),
                    startTime = slot
                )

                Log.d(TAG, "✅ Reagendamento bem-sucedido!")
                _uiState.update {
                    it.copy(
                        successMessage = "Agendamento reagendado com sucesso",
                        showRescheduleDialog = false,
                        rescheduleAppointment = null
                    )
                }

                // Recarrega a lista após reagendar
                fetchAppointments()
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao reagendar: ${e.message}", e)
                val errorMsg = classifyError(e)
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            }
        }
    }

    private fun classifyError(e: Exception): String = when (e) {
        is UnknownHostException -> "Servidor não encontrado. Verifique a conexão."
        is ConnectException -> "Não foi possível conectar ao servidor."
        is SocketTimeoutException -> "Tempo de resposta excedido. Tente novamente."
        is HttpException -> when (e.code()) {
            401, 403 -> "Sessão expirada. Faça login novamente."
            else -> "Erro ${e.code()} ao buscar agendamentos."
        }
        else -> e.message ?: "Erro ao carregar agendamentos"
    }
}