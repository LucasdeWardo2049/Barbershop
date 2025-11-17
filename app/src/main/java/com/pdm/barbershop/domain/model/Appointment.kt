package com.pdm.barbershop.domain.model

data class Appointment(
    val appointmentId: Int,
    val barberId: Int,
    val serviceId: Int,
    val clientId: Int,
    val startTime: String,
    val endTime: String,
    val status: String,
    val totalPrice: Double?,
    val clientName: String,
    val barberName: String,
    val serviceName: String
)
