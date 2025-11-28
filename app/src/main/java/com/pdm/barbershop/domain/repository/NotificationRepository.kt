package com.pdm.barbershop.domain.repository

import com.pdm.barbershop.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    val notifications: Flow<List<Notification>>
    suspend fun addNotification(title: String, message: String)
    suspend fun markAsRead(id: String)
    suspend fun clearAll()
}
