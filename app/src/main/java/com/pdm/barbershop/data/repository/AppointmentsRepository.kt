package com.pdm.barbershop.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.pdm.barbershop.data.remote.ApiService
import com.pdm.barbershop.data.remote.dto.AppointmentDto
import com.pdm.barbershop.data.remote.dto.RescheduleAppointmentRequest
import com.pdm.barbershop.domain.model.Appointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppointmentsRepository @Inject constructor(
    private val api: ApiService
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun listMyAppointments(): List<Appointment> = withContext(Dispatchers.IO) {
        api.getMyAppointments().map { it.toDomain() }
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
            clientName = "Nome do Cliente", // Placeholder
            barberName = barberName ?: "Barbeiro #${barberId}",
            serviceName = serviceName ?: "Serviço #${serviceId}"
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun rescheduleAppointment(
        appointmentId: Long,
        barberId: Long,
        serviceId: Long,
        startTime: String // Formato UTC
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