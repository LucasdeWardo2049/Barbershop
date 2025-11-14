package com.pdm.barbershop.ui.feature.schedule

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.remote.dto.BookAppointmentRequest
import com.pdm.barbershop.data.repository.ScheduleRepository
import com.pdm.barbershop.domain.model.Barber
import com.pdm.barbershop.domain.model.Service
import com.pdm.barbershop.util.DateTimeUtils
import com.pdm.barbershop.util.DateTimeUtils.toUtcZ
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
    private val repo: ScheduleRepository
) : ViewModel() {
    private val _ui = MutableStateFlow(ScheduleUiState())
    val ui = _ui.asStateFlow()

    private val zone = ZoneId.of("America/Sao_Paulo")

    init { loadServicesAndBarbers() }

    private fun loadServicesAndBarbers() {
        _ui.update { it.copy(loading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val services = repo.loadServices()
                val barbers = repo.loadBarbers()
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
                // Filtrar horários passados (principalmente para hoje)
                val filtered = slots.filter { iso ->
                    !DateTimeUtils.isIsoPast(iso)
                }
                _ui.update { it.copy(availability = filtered, loadingSlots = false) }
            } catch (e: Exception) {
                _ui.update { it.copy(errorMessage = classifyError(e), loadingSlots = false) }
            }
        }
    }

    fun book(clientIdProvider: () -> Long) {
        val s = _ui.value
        val svc = s.selectedService ?: return
        val barber = s.selectedBarber ?: return
        val timeIso = s.selectedTime ?: return // ISO completo
        _ui.update { it.copy(loading = true, errorMessage = null, bookingSuccess = false) }
        viewModelScope.launch {
            try {
                // Valida horário passado
                if (DateTimeUtils.isIsoPast(timeIso)) {
                    _ui.update { it.copy(
                        errorMessage = "Horário selecionado já passou. Escolha outro.",
                        loading = false
                    ) }
                    return@launch
                }

                // Converter para formato estrito UTC com 'Z' sem milissegundos
                val startUtcZ = toUtcZ(timeIso)
                Log.d("ScheduleVM", "Booking UTC Z: $startUtcZ (from $timeIso)")

                val req = BookAppointmentRequest(
                    clientId = clientIdProvider(),
                    barberId = barber.id.toLong(),
                    serviceId = svc.id.toLong(),
                    startTime = startUtcZ,
                    tz = zone.id
                )
                repo.book(req)
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
        is UnknownHostException -> "Servidor não encontrado. Verifique a URL."
        is ConnectException -> "Não foi possível conectar ao servidor."
        is SocketTimeoutException -> "Tempo de resposta excedido. Tente novamente."
        is HttpException -> when (e.code()) {
            401,403 -> "Não autorizado. Faça login novamente."
            409 -> "Horário indisponível. Escolha outro ou recarregue a lista."
            else -> "Erro ${e.code()} ao comunicar com o servidor."
        }
        else -> e.message ?: "Erro desconhecido"
    }
}
