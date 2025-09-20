package com.pdm.barbershop.domain.model

data class Appointment(
    val id: String,
    val date: String,
    val time: String,
    val serviceName: String,
    val barberName: String,
    val status: String
)