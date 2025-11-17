package com.pdm.barbershop.domain.model

import com.pdm.barbershop.data.remote.dto.UserDto

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

fun UserDto.toDomain(): User {
    return User(
        userId = userId.toString(),
        name = name,
        email = email ?: "",
        phone = phone ?: "",
        role = role,
        clientId = clientId,
        barberId = barberId,
        avatarUrl = avatarUrl
    )
}