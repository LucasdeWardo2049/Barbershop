package com.pdm.barbershop.data.repository

import com.pdm.barbershop.domain.model.Notification
import com.pdm.barbershop.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    override val notifications: Flow<List<Notification>> = _notifications.asStateFlow()

    override suspend fun addNotification(title: String, message: String) {
        val newNotification = Notification(
            title = title,
            message = message
        )
        _notifications.update { currentList ->
            listOf(newNotification) + currentList
        }
    }

    override suspend fun markAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    override suspend fun clearAll() {
        _notifications.value = emptyList()
    }
}
