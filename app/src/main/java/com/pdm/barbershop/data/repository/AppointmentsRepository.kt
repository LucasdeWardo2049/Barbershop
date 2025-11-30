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
        // Como o endpoint getMyAppointments no ApiService não tem o param tz, precisamos adicionar lá.
        // Vou assumir que você prefere que eu edite o ApiService também, ou apenas use o padrão do backend.
        // Como o backend disse que o padrão já é America/Manaus, não precisaria mudar se o backend estiver certo.
        // Mas se o problema persiste, talvez o front deva explicitar.
        // Vou editar o ApiService para aceitar tz opcional e passar aqui.
        api.getMyAppointments(tz = "America/Manaus").map { it.toDomain() }
    }

    suspend fun cancelAppointment(appointmentId: Long) = withContext(Dispatchers.IO) {
        api.cancelMyAppointment(appointmentId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun rescheduleAppointment(
        appointmentId: Long,
        barberId: Long,
        serviceId: Long,
        startTime: String
    ): Appointment = withContext(Dispatchers.IO) {
        val request = RescheduleRequest(
            barberId = barberId,
            serviceId = serviceId,
            startTime = startTime
        )
        api.rescheduleMyAppointment(appointmentId, request).toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun AppointmentDto.toDomain(): Appointment {
        return Appointment(
            appointmentId = (appointmentId ?: 0L).toInt(),
            barberId = barberId.toInt(),
            serviceId = serviceId.toInt(),
            clientId = clientId.toInt(),
            // O backend retorna OffsetDateTime no JSON. O Gson converte para objeto. 
            // startTime.toString() preserva o offset se o objeto tiver essa info.
            // Se o backend retornou com offset correto (devido ao param tz), toString() deve ser suficiente.
            startTime = startTime.toString(),
            endTime = endTime?.toString() ?: "",
            status = status,
            totalPrice = totalPrice?.toDouble(),
            clientName = clientName ?: "Nome do Cliente", 
            barberName = barberName ?: "Barbeiro #${barberId}",
            serviceName = serviceName ?: "Serviço #${serviceId}"
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun rescheduleAppointment(
        appointmentId: Long,
        barberId: Long,
        serviceId: Long,
        startTime: String // Formato UTC ou Offset
    ): Result<Appointment> = withContext(Dispatchers.IO) {
        try {
            val request = RescheduleAppointmentRequest(
                barberId = barberId,
                serviceId = serviceId,
                startTime = startTime
            )
            val updated = api.rescheduleAppointment(appointmentId, request)
            Result.success(updated.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelAppointment(appointmentId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = api.cancelAppointment(appointmentId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Falha ao cancelar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}