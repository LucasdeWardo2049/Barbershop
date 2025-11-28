package com.pdm.barbershop.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val isRead: Boolean = false
)
