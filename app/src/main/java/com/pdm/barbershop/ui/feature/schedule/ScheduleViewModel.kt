package com.pdm.barbershop.ui.feature.schedule

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.repository.ScheduleRepository
import com.pdm.barbershop.domain.model.Barber
import com.pdm.barbershop.domain.model.Service
import com.pdm.barbershop.domain.repository.NotificationRepository
import com.pdm.barbershop.domain.repository.UserRepository
import com.pdm.barbershop.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import retrofit2.HttpException

data class ScheduleUiState(
    val services: List<Service> = emptyList(),
    val barbers: List<Barber> = emptyList(),
    val availableDates: List<LocalDate> = emptyList(),
    val availability: List<String> = emptyList(),
    val selectedService: Service? = null,
    val selectedBarber: Barber? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: String? = null,
    val bookingSuccess: Boolean = false,
    val loading: Boolean = false,
    val loadingSlots: Boolean = false,
    val errorMessage: String? = null
) {
    val canBook: Boolean get() = selectedService != null && selectedBarber != null && selectedDate != null && selectedTime != null
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repo: ScheduleRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _ui = MutableStateFlow(ScheduleUiState())
    val ui = _ui.asStateFlow()

    // Ajustado para Manaus conforme solicitado para o ambiente de teste
    private val zone = ZoneId.of("America/Manaus")

    init { loadServicesAndBarbers() }

    private fun loadServicesAndBarbers() {
        _ui.update { it.copy(loading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val services = repo.loadServices()
                val barbers = repo.loadBarbers()
                // Gera datas baseadas no fuso horário correto
                val dates = List(14) { LocalDate.now(zone).plusDays(it.toLong()) }
                _ui.update { it.copy(services = services, barbers = barbers, availableDates = dates, loading = false) }
            } catch (e: Exception) {
                _ui.update { it.copy(errorMessage = classifyError(e), loading = false) }
            }
        }
    }

    fun selectService(service: Service) {
        _ui.update { it.copy(
            selectedService = service,
            selectedBarber = null,
            selectedDate = null,
            selectedTime = null,
            availability = emptyList(),
            bookingSuccess = false,
            errorMessage = null
        ) }
    }

    fun selectBarber(barber: Barber) {
        _ui.update { it.copy(
            selectedBarber = barber,
            selectedDate = null,
            selectedTime = null,
            availability = emptyList(),
            bookingSuccess = false,
            errorMessage = null
        ) }
    }

    fun selectDate(date: LocalDate) {
        _ui.update { it.copy(selectedDate = date, selectedTime = null, availability = emptyList(), errorMessage = null) }
        loadAvailability()
    }

    fun selectTime(time: String) { _ui.update { it.copy(selectedTime = time) } }

    private fun loadAvailability() {
        val state = _ui.value
        val svc = state.selectedService ?: return
        val barber = state.selectedBarber ?: return
        val date = state.selectedDate ?: return
        _ui.update { it.copy(loadingSlots = true, errorMessage = null, availability = emptyList()) }
        viewModelScope.launch {
            try {
                val slots = repo.loadAvailability(barber.id.toLong(), svc.id.toLong(), date.toString())
                Log.d("ScheduleVM", "Slots recebidos para $date: $slots")
                
                // Filtrar horários passados
                // O backend deve retornar ISO8601 com Offset. DateTimeUtils.isIsoPast deve lidar com isso,
                // mas garantimos que estamos comparando corretamente com o "agora"
                val filtered = slots.filter { iso ->
                    !DateTimeUtils.isIsoPast(iso)
                }
                Log.d("ScheduleVM", "Slots filtrados: $filtered")
                
                _ui.update { it.copy(availability = filtered, loadingSlots = false) }
            } catch (e: Exception) {
                _ui.update { it.copy(errorMessage = classifyError(e), loadingSlots = false) }
            }
        }
    }

    fun book() {
        val s = _ui.value
        val svc = s.selectedService ?: return
        val barber = s.selectedBarber ?: return
        val timeIso = s.selectedTime ?: return // ISO completo

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, errorMessage = null, bookingSuccess = false) }
            try {
                // Obter clientId do usuário logado
                val user = userRepository.currentUser.value
                val clientId = user?.clientId ?: user?.userId?.toLongOrNull()

                if (clientId == null) {
                    _ui.update { it.copy(
                        errorMessage = "Usuário não autenticado. Faça login novamente.",
                        loading = false
                    ) }
                    return@launch
                }

                // Logs para diagnóstico
                Log.d("BOOKING", "═══════════════════════════════")
                Log.d("BOOKING", "📅 Slot selecionado (raw): $timeIso")
                Log.d("BOOKING", "🌍 Fuso horário: $zone")
                Log.d("BOOKING", "⏰ Horário atual: ${java.time.Instant.now()}")
                Log.d("BOOKING", "🔍 isIsoPast? ${DateTimeUtils.isIsoPast(timeIso)}")

                // Valida horário passado (com margem de 2 minutos)
                if (DateTimeUtils.isIsoPast(timeIso, toleranceSeconds = 0)) {
                    Log.w("BOOKING", "❌ Horário no passado detectado!")
                    _ui.update { it.copy(
                        errorMessage = "Horário selecionado já passou. Escolha outro.",
                        loading = false
                    ) }
                    return@launch
                }

                // Converte para UTC Z para garantir formato aceito pelo backend
                val utcZ = DateTimeUtils.toUtcZ(timeIso)
                Log.d("BOOKING", "📤 Enviando para backend: $utcZ")
                Log.d("BOOKING", "👤 ClientId: $clientId")
                Log.d("BOOKING", "💈 BarberId: ${barber.id}")
                Log.d("BOOKING", "✂️ ServiceId: ${svc.id}")
                Log.d("BOOKING", "═══════════════════════════════")

                repo.book(
                    clientId = clientId,
                    barberId = barber.id.toLong(),
                    serviceId = svc.id.toLong(),
                    startTime = utcZ // Envia em UTC Z
                )
                
                notificationRepository.addNotification(
                    title = "Agendamento Confirmado",
                    message = "Seu agendamento para ${svc.name} com ${barber.name} foi confirmado para ${DateTimeUtils.labelFromIso(timeIso, zone)}."
                )

                _ui.update { it.copy(bookingSuccess = true, loading = false) }
            } catch (e: Exception) {
                val msg = classifyError(e)
                if (e is HttpException && e.code() == 409) {
                    loadAvailability()
                }
                _ui.update { it.copy(errorMessage = msg, loading = false, bookingSuccess = false) }
            }
        }
    }

    fun consumeBookingSuccess() { _ui.update { it.copy(bookingSuccess = false) } }

    private fun classifyError(e: Exception): String = when (e) {
        is IllegalArgumentException -> {
            // Horário no passado (validação local)
            "Horário já passou. Por favor, escolha um horário futuro."
        }
        is UnknownHostException -> "Servidor não encontrado. Verifique a URL."
        is ConnectException -> "Não foi possível conectar ao servidor."
        is SocketTimeoutException -> "Tempo de resposta excedido. Tente novamente."
        is HttpException -> when (e.code()) {
            401, 403 -> "Não autorizado. Faça login novamente."
            409 -> {
                Log.w("BOOKING", "❌ Conflito HTTP 409: Horário já reservado")
                "Horário já reservado por outro cliente. Escolha outro horário."
            }
            400 -> {
                val body = e.response()?.errorBody()?.string()
                Log.e("BOOKING", "❌ Erro 400: $body")
                "Formato de horário inválido. Tente novamente."
            }
            else -> {
                Log.e("BOOKING", "❌ Erro HTTP ${e.code()}")
                "Erro ${e.code()} ao comunicar com o servidor."
            }
        }
        else -> {
            Log.e("BOOKING", "❌ Erro inesperado: ${e.message}", e)
            e.message ?: "Erro desconhecido"
        }
    }
}
