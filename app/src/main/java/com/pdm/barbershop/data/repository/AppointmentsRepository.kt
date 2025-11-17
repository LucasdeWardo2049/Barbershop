package com.pdm.barbershop.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.pdm.barbershop.data.remote.ApiService
import com.pdm.barbershop.data.remote.dto.AppointmentDto
import com.pdm.barbershop.domain.model.Appointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
        val zoneId = ZoneId.of("America/Sao_Paulo")
        val localDateTime = startTime.atZoneSameInstant(zoneId).toLocalDateTime()

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        return Appointment(
            id = (appointmentId ?: 0L).toString(),
            date = localDateTime.format(dateFormatter),
            time = localDateTime.format(timeFormatter),
            serviceName = serviceName ?: "Serviço #${serviceId}",
            barberName = barberName ?: "Barbeiro #${barberId}",
            status = status
        )
    }
}
