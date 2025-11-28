package com.pdm.barbershop.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pdm.barbershop.data.remote.ApiService
import com.pdm.barbershop.data.remote.dto.AppointmentDto
import com.pdm.barbershop.domain.model.Appointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class AppointmentsRepository @Inject constructor(
    private val api: ApiService
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun listMyAppointments(): List<Appointment> = withContext(Dispatchers.IO) {
        api.getMyAppointments().map { it.toDomain() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cancelMyAppointment(id: Long) = withContext(Dispatchers.IO) {
        try {
            val response = api.cancelMyAppointment(id)
            when (response.code()) {
                204 -> {
                    // Success - appointment cancelled
                    Log.d("AppointmentsRepo", "Agendamento $id cancelado com sucesso")
                }
                404 -> {
                    // Not found - appointment doesn't exist or doesn't belong to user
                    Log.w("AppointmentsRepo", "Agendamento $id não encontrado")
                    throw IllegalArgumentException("Agendamento não encontrado ou você não tem permissão para cancelá-lo")
                }
                else -> {
                    Log.e("AppointmentsRepo", "Erro ao cancelar agendamento: ${response.code()}")
                    throw HttpException(response)
                }
            }
        } catch (e: HttpException) {
            Log.e("AppointmentsRepo", "Erro HTTP ao cancelar agendamento $id", e)
            throw e
        } catch (e: Exception) {
            Log.e("AppointmentsRepo", "Erro ao cancelar agendamento $id", e)
            throw e
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
            clientName = clientName ?: "Cliente #${clientId}",
            barberName = barberName ?: "Barbeiro #${barberId}",
            serviceName = serviceName ?: "Serviço #${serviceId}"
        )
    }
}