package com.pdm.barbershop.data.repository

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import com.pdm.barbershop.data.remote.ApiService
import com.pdm.barbershop.data.remote.dto.AppointmentDto
import com.pdm.barbershop.data.remote.dto.ServiceDto
import com.pdm.barbershop.data.remote.dto.UserDto
import com.pdm.barbershop.data.remote.dto.WorkingHourResponse
import com.pdm.barbershop.data.remote.dto.WorkingHoursRequest
import com.pdm.barbershop.domain.model.Appointment
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
            val idToUse = user.barberId?.toString() ?: user.userId.toString()
            
            Barber(
                id = idToUse,
                name = user.name,
                rating = 0.0,
                imageUrl = user.avatarUrl
            )
        }
    }

    suspend fun loadAvailability(barberId: Long, serviceId: Long, dateISO: String): List<String> =
        withContext(Dispatchers.IO) {
            val slots = api.getAvailability(barberId, serviceId, dateISO)
            slots.map { it.start.toString() }.distinct()
        }

    suspend fun getAvailability(barberId: Long, serviceId: Long, date: java.time.LocalDate): List<String> =
        withContext(Dispatchers.IO) {
            val dateISO = date.toString()
            val slots = api.getAvailability(barberId, serviceId, dateISO)
            slots.map { it.start.toString() }.distinct()
        }

    suspend fun book(clientId: Long, barberId: Long, serviceId: Long, startTime: String): AppointmentDto =
        withContext(Dispatchers.IO) {
            val req = com.pdm.barbershop.data.remote.dto.AppointmentRequest(
                clientId = clientId,
                barberId = barberId,
                serviceId = serviceId,
                startTime = startTime,
                status = "SCHEDULED"
            )

            try {
                Log.d("ScheduleRepo", "📤 Enviando requisição de agendamento...")
                val resp = api.bookAppointment(req)
                Log.d("ScheduleRepo", "✅ Agendamento criado: ID ${resp.appointmentId}")
                resp
            } catch (e: HttpException) {
                val raw = e.response()?.errorBody()?.string()
                Log.e("ScheduleRepo", "❌ HTTP ${e.code()} response: $raw", e)
                throw e
            } catch (e: Exception) {
                Log.e("ScheduleRepo", "❌ Erro inesperado: ${e.message}", e)
                throw e
            }
        }
        
    suspend fun addWorkingHours(barberId: Long, dayOfWeek: Int, startTime: String, endTime: String) = 
        withContext(Dispatchers.IO) {
            val req = WorkingHoursRequest(
                barberId = barberId,
                dayOfWeek = dayOfWeek,
                startTime = startTime,
                endTime = endTime
            )
            val resp = api.addWorkingHours(req)
            if (!resp.isSuccessful) {
                throw HttpException(resp)
            }
        }

    suspend fun searchWorkingHours(barberId: Long, dayOfWeek: Int? = null): List<WorkingHourResponse> =
        withContext(Dispatchers.IO) {
            api.searchWorkingHours(barberId, dayOfWeek)
        }

    suspend fun updateWorkingHours(id: Long, request: WorkingHoursRequest): WorkingHourResponse =
        withContext(Dispatchers.IO) {
            api.updateWorkingHours(id, request)
        }

    suspend fun deleteWorkingHours(id: Long) =
        withContext(Dispatchers.IO) {
            val resp = api.deleteWorkingHours(id)
            if (!resp.isSuccessful) {
                throw HttpException(resp)
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

    private fun AppointmentDto.toDomain(): Appointment =
        Appointment(
            appointmentId = appointmentId?.toInt() ?: 0,
            barberId = barberId.toInt(),
            serviceId = serviceId.toInt(),
            clientId = clientId.toInt(),
            startTime = startTime.toString(),
            endTime = endTime?.toString() ?: "",
            status = status,
            totalPrice = totalPrice?.toDouble(),
            clientName = clientName ?: "Unknown",
            barberName = barberName ?: "Unknown",
            serviceName = serviceName ?: "Unknown"
        )
}
