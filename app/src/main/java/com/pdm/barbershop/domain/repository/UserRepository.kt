package com.pdm.barbershop.domain.repository

import com.pdm.barbershop.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUser: StateFlow<User?>
    suspend fun fetchUser()
    suspend fun clearUser()
    suspend fun updateUser(user: User)
}
