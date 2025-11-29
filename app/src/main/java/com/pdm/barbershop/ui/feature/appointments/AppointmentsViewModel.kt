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
import com.pdm.barbershop.util.DateTimeUtils
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

    private val zone = ZoneId.of("America/Sao_Paulo")

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

    private fun classifyError(e: Exception): String = when (e) {
        is UnknownHostException -> "Servidor não encontrado. Verifique a conexão."
        is ConnectException -> "Não foi possível conectar ao servidor."
        is SocketTimeoutException -> "Tempo de resposta excedido. Tente novamente."
        is HttpException -> when (e.code()) {
            401, 403 -> "Sessão expirada. Faça login novamente."
            404 -> "Agendamento não encontrado ou não pertence a você."
            400 -> "Não é possível realizar esta operação. Verifique o status do agendamento."
            else -> "Erro ${e.code()} ao processar solicitação."
        }
        else -> e.message ?: "Erro ao processar solicitação"
    }

    fun cancelAppointment(appointmentId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            appointmentsRepository.cancelAppointment(appointmentId.toLong())
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Agendamento cancelado com sucesso") }
                    // Recarrega a lista após cancelar
                    fetchAppointments()
                }
                .onFailure { e ->
                    val errorMsg = classifyError(e as Exception)
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
        }
    }

    fun rescheduleAppointment(
        appointmentId: Int,
        barberId: Int,
        serviceId: Int,
        startTime: String // UTC format
    ) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            appointmentsRepository.rescheduleAppointment(
                appointmentId.toLong(),
                barberId.toLong(),
                serviceId.toLong(),
                startTime
            )
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Agendamento reagendado com sucesso") }
                    // Recarrega a lista após reagendar
                    fetchAppointments()
                }
                .onFailure { e ->
                    val errorMsg = classifyError(e as Exception)
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun setFilter(filter: AppointmentFilter) {
        _uiState.update { it.copy(currentFilter = filter) }
    }

    // Reschedule dialog methods
    fun openRescheduleDialog(appointment: Appointment) {
        Log.d(TAG, "📅 REAGENDAR: Abrindo dialog para agendamento #${appointment.appointmentId}")
        Log.d(TAG, "📅 REAGENDAR: Serviço: ${appointment.serviceName}")
        Log.d(TAG, "📅 REAGENDAR: Barbeiro: ${appointment.barberName}")
        Log.d(TAG, "📅 REAGENDAR: Status atual: ${appointment.status}")

        val dates = List(14) { LocalDate.now(zone).plusDays(it.toLong()) }
        Log.d(TAG, "📅 REAGENDAR: Gerando ${dates.size} datas disponíveis")

        _uiState.update {
            it.copy(
                showRescheduleDialog = true,
                rescheduleAppointment = appointment,
                availableDates = dates,
                selectedDate = null,
                availableSlots = emptyList(),
                selectedSlot = null
            )
        }
        Log.d(TAG, "📅 REAGENDAR: Dialog aberto com sucesso")
    }

    fun closeRescheduleDialog() {
        Log.d(TAG, "📅 REAGENDAR: Fechando dialog")
        _uiState.update {
            it.copy(
                showRescheduleDialog = false,
                rescheduleAppointment = null,
                selectedDate = null,
                availableSlots = emptyList(),
                selectedSlot = null
            )
        }
    }

    fun selectRescheduleDate(date: LocalDate) {
        Log.d(TAG, "📅 REAGENDAR: Data selecionada: $date")
        _uiState.update { it.copy(selectedDate = date, selectedSlot = null, availableSlots = emptyList()) }
        loadAvailabilityForReschedule()
    }

    fun selectRescheduleSlot(slot: String) {
        Log.d(TAG, "📅 REAGENDAR: Horário selecionado: $slot")
        _uiState.update { it.copy(selectedSlot = slot) }
    }

    private fun loadAvailabilityForReschedule() {
        val state = _uiState.value
        val appointment = state.rescheduleAppointment ?: return
        val date = state.selectedDate ?: return

        Log.d(TAG, "📅 REAGENDAR: Carregando horários disponíveis...")
        Log.d(TAG, "📅 REAGENDAR: Barbeiro ID: ${appointment.barberId}")
        Log.d(TAG, "📅 REAGENDAR: Serviço ID: ${appointment.serviceId}")
        Log.d(TAG, "📅 REAGENDAR: Data: $date")

        _uiState.update { it.copy(loadingSlots = true, errorMessage = null, availableSlots = emptyList()) }
        viewModelScope.launch {
            try {
                val slots = scheduleRepository.loadAvailability(
                    barberId = appointment.barberId.toLong(),
                    serviceId = appointment.serviceId.toLong(),
                    dateISO = date.toString()
                )
                Log.d(TAG, "📅 REAGENDAR: API retornou ${slots.size} horários")

                // Filtrar horários passados
                val filtered = slots.filter { iso -> !DateTimeUtils.isIsoPast(iso) }
                Log.d(TAG, "📅 REAGENDAR: Após filtrar horários passados: ${filtered.size} horários disponíveis")

                _uiState.update { it.copy(availableSlots = filtered, loadingSlots = false) }
            } catch (e: Exception) {
                Log.e(TAG, "❌ REAGENDAR: Erro ao carregar horários: ${e.message}", e)
                _uiState.update { it.copy(errorMessage = classifyError(e), loadingSlots = false) }
            }
        }
    }

    fun confirmReschedule() {
        val state = _uiState.value
        val appointment = state.rescheduleAppointment ?: return
        val slot = state.selectedSlot ?: return

        Log.d(TAG, "✅ REAGENDAR: Confirmando reagendamento...")
        Log.d(TAG, "✅ REAGENDAR: Agendamento ID: ${appointment.appointmentId}")
        Log.d(TAG, "✅ REAGENDAR: Barbeiro ID: ${appointment.barberId}")
        Log.d(TAG, "✅ REAGENDAR: Serviço ID: ${appointment.serviceId}")
        Log.d(TAG, "✅ REAGENDAR: Horário selecionado (ISO): $slot")

        // Converter para UTC format
        val startUtcZ = DateTimeUtils.toUtcZ(slot)
        Log.d(TAG, "✅ REAGENDAR: Horário convertido para UTC: $startUtcZ")

        // Mostra loading e mantém dialog aberto
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
        Log.d(TAG, "✅ REAGENDAR: Loading ativado, chamando API...")

        viewModelScope.launch {
            appointmentsRepository.rescheduleAppointment(
                appointmentId = appointment.appointmentId.toLong(),
                barberId = appointment.barberId.toLong(),
                serviceId = appointment.serviceId.toLong(),
                startTime = startUtcZ
            )
                .onSuccess {
                    Log.d(TAG, "🎉 REAGENDAR: Sucesso! Agendamento atualizado")
                    Log.d(TAG, "🎉 REAGENDAR: Novo horário: ${it.startTime}")

                    // Fecha dialog e mostra sucesso
                    closeRescheduleDialog()
                    _uiState.update { it.copy(
                        isLoading = false,
                        successMessage = "Agendamento reagendado com sucesso"
                    ) }

                    Log.d(TAG, "🎉 REAGENDAR: Recarregando lista de agendamentos...")
                    // Recarrega a lista
                    fetchAppointments()
                }
                .onFailure { e ->
                    Log.e(TAG, "❌ REAGENDAR: Erro ao reagendar: ${e.message}", e)

                    if (e is retrofit2.HttpException) {
                        Log.e(TAG, "❌ REAGENDAR: Código HTTP: ${e.code()}")
                        Log.e(TAG, "❌ REAGENDAR: Response body: ${e.response()?.errorBody()?.string()}")
                    }

                    // Mantém dialog aberto e mostra erro
                    val errorMsg = classifyError(e as Exception)
                    Log.e(TAG, "❌ REAGENDAR: Mensagem de erro exibida: $errorMsg")

                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    ) }
                }
        }
    }
}