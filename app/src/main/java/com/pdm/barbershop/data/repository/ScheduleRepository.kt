package com.pdm.barbershop.data.repository

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import com.pdm.barbershop.data.remote.ApiService
import com.pdm.barbershop.data.remote.dto.AppointmentDto
import com.pdm.barbershop.data.remote.dto.ServiceDto
import com.pdm.barbershop.data.remote.dto.UserDto
import com.pdm.barbershop.domain.model.Barber
import com.pdm.barbershop.domain.model.Service
import com.pdm.barbershop.util.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun loadServices(): List<Service> = withContext(Dispatchers.IO) {
        val page = api.getServices(size = 100, active = true)
        page.content.map { it.toDomain() }
    }

    suspend fun loadBarbers(): List<Barber> = withContext(Dispatchers.IO) {
        api.getBarbers().map { user ->
            Barber(
                id = user.userId.toString(),
                name = user.name,
                rating = 0.0,
                imageUrl = user.avatarUrl
            )
        }
    }

    suspend fun loadAvailability(barberId: Long, serviceId: Long, dateISO: String): List<String> =
        withContext(Dispatchers.IO) {
            val slots = api.getAvailability(barberId, serviceId, dateISO)
            // Preserve ISO completo (com offset) em vez de extrair apenas HH:mm
            slots.map { it.start.toString() }.distinct()
        }

    suspend fun getAvailability(barberId: Long, serviceId: Long, date: java.time.LocalDate): List<String> =
        withContext(Dispatchers.IO) {
            val dateISO = date.toString() // YYYY-MM-DD
            val slots = api.getAvailability(barberId, serviceId, dateISO)
            slots.map { it.start.toString() }.distinct()
        }

    suspend fun book(clientId: Long, barberId: Long, serviceId: Long, startTime: String): AppointmentDto =
        withContext(Dispatchers.IO) {
            // Validação adicional: verificar se startTime não está no passado
            if (DateTimeUtils.isIsoPast(startTime)) {
                Log.w("Booking", "Tentativa de agendar horário no passado: $startTime")
                throw IllegalArgumentException("Horário no passado. Selecione um horário futuro.")
            }

            val req = com.pdm.barbershop.data.remote.dto.AppointmentRequest(
                clientId = clientId,
                barberId = barberId,
                serviceId = serviceId,
                startTime = startTime,
                status = "SCHEDULED"
            )

            Log.d("Booking", "Request: clientId=$clientId barberId=$barberId serviceId=$serviceId")
            Log.d("Booking", "startTime='$startTime' status=SCHEDULED")
            try {
                val resp = api.bookAppointment(req)
                Log.d("Booking", "Success: appointmentId=${resp.appointmentId} status=${resp.status}")
                resp
            } catch (e: HttpException) {
                val raw = e.response()?.errorBody()?.string()
                Log.e("Booking", "HTTP ${e.code()} response body: $raw", e)
                throw e
            } catch (e: Exception) {
                Log.e("Booking", "Unexpected error: ${e.message}", e)
                throw e
            }
        }

    private fun ServiceDto.toDomain(): Service =
        Service(
            id = serviceId.toString(),
            name = name,
            price = (price ?: java.math.BigDecimal.ZERO).toDouble(),
            durationInMinutes = (durationMinutes ?: 30),
            icon = Icons.Default.ContentCut
        )
}
