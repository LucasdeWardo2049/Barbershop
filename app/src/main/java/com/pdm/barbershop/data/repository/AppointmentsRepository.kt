package com.pdm.barbershop.data.repository

import android.os.Build
import com.pdm.barbershop.data.remote.ApiService
import com.pdm.barbershop.data.remote.dto.AppointmentDto
import com.pdm.barbershop.domain.model.Appointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppointmentsRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun listByClient(clientId: Long): List<Appointment> = withContext(Dispatchers.IO) {
        api.getAppointmentsByClient(clientId).map { it.toDomain() }
    }

    private fun AppointmentDto.toDomain(): Appointment {
        val iso = startTime.toString() // e.g. 2025-11-13T12:30:00Z
        val day = iso.substring(8, 10)
        val month = iso.substring(5, 7)
        val time = iso.substring(11, 16) // HH:mm
        return Appointment(
            id = (appointmentId ?: 0L).toString(),
            date = "$day/$month",
            time = time,
            serviceName = "Serviço #${serviceId}",
            barberName = "Barbeiro #${barberId}",
            status = status
        )
    }
}
