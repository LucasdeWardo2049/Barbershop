package com.pdm.barbershop.domain.model

data class User(
    val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val clientId: Long? = null,
    val barberId: Long? = null,
    val avatarUrl: String? = null
)