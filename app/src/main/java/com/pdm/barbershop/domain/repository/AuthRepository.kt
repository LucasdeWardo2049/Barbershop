package com.pdm.barbershop.domain.repository

import com.pdm.barbershop.data.remote.AuthRequest
import com.pdm.barbershop.data.remote.AuthResponse
import com.pdm.barbershop.data.remote.RegisterRequest
import com.pdm.barbershop.domain.model.User

interface AuthRepository {
    suspend fun login(authRequest: AuthRequest): AuthResponse
    suspend fun register(registerRequest: RegisterRequest): User
}