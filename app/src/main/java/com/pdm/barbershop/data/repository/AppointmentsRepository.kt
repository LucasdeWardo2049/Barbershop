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
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun listMyAppointments(): List<Appointment> = withContext(Dispatchers.IO) {
        api.getMyAppointments().map { it.toDomain() }
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
            startTime = startTime.toString(),
            endTime = endTime?.toString() ?: "",
            status = status,
            totalPrice = totalPrice?.toDouble(),
            clientName = "Nome do Cliente", // Placeholder
            barberName = barberName ?: "Barbeiro #${barberId}",
            serviceName = serviceName ?: "Serviço #${serviceId}"
        )
    }
}