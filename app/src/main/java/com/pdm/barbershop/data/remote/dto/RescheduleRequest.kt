package com.pdm.barbershop.data.remote.dto

data class RescheduleRequest(
    val barberId: Long,
    val serviceId: Long,
    val startTime: String
)

data class RescheduleAppointmentRequest(
    val barberId: Long,
    val serviceId: Long,
    val startTime: String
)
