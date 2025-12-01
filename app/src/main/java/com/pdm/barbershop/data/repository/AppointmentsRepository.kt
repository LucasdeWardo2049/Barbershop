package com.pdm.barbershop.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.pdm.barbershop.data.remote.ApiService
import com.pdm.barbershop.data.remote.dto.AppointmentDto
import com.pdm.barbershop.data.remote.dto.RescheduleRequest
import com.pdm.barbershop.domain.model.Appointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppointmentsRepository @Inject constructor(
    private val api: ApiService
) {
    // Adicionamos o query param "tz" para garantir que a data venha no fuso correto
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun listMyAppointments(): List<Appointment> = withContext(Dispatchers.IO) {
        api.getMyAppointments(tz = "America/Manaus").map { it.toDomain() }
    }

    suspend fun cancelAppointment(appointmentId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Tenta usar o endpoint que retorna Response<Unit>
            val response = api.cancelAppointment(appointmentId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                // Se falhar ou se o endpoint não for o correto, tenta o outro endpoint void (legacy)
                // Mas como precisamos retornar Result, vamos assumir que o novo é o correto.
                // Se quiser suportar o legado:
                // api.cancelMyAppointment(appointmentId)
                // Result.success(true)
                Result.failure(Exception("Falha ao cancelar: ${response.code()}"))
            }
        } catch (e: Exception) {
             try {
                // Fallback para o método antigo se o novo falhar (ex: 404 ou exception de rota)
                api.cancelMyAppointment(appointmentId)
                Result.success(true)
            } catch (e2: Exception) {
                Result.failure(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun rescheduleAppointment(
        appointmentId: Long,
        barberId: Long,
        serviceId: Long,
        startTime: String
    ): Result<Appointment> = withContext(Dispatchers.IO) {
        try {
            val request = RescheduleRequest(
                barberId = barberId,
                serviceId = serviceId,
                startTime = startTime
            )
            // Tentando usar o endpoint unificado. Se houver conflito de endpoints no ApiService, 
            // escolha um. Aqui estou usando o rescheduleMyAppointment que retorna DTO direto.
            val dto = api.rescheduleMyAppointment(appointmentId, request)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
             // Fallback ou erro direto
             Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun AppointmentDto.toDomain(): Appointment {
        return Appointment(
            appointmentId = (appointmentId ?: 0L).toInt(),
            barberId = barberId.toInt(),
            serviceId = serviceId.toInt(),
            clientId = clientId.toInt(),
            startTime = startTime.toString(),
            endTime = endTime?.toString() ?: "",
            status = status,
            totalPrice = totalPrice?.toDouble(),
            clientName = clientName ?: "Nome do Cliente", 
            barberName = barberName ?: "Barbeiro #${barberId}",
            serviceName = serviceName ?: "Serviço #${serviceId}"
        )
    }
}
