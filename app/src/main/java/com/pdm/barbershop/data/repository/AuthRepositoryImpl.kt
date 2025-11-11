package com.pdm.barbershop.data.repository

import com.pdm.barbershop.data.remote.AuthApiService
import com.pdm.barbershop.data.remote.AuthRequest
import com.pdm.barbershop.data.remote.AuthResponse
import com.pdm.barbershop.data.remote.RegisterRequest
import com.pdm.barbershop.domain.model.User
import com.pdm.barbershop.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService
) : AuthRepository {

    override suspend fun login(authRequest: AuthRequest): AuthResponse {
        return apiService.login(authRequest)
    }

    override suspend fun register(registerRequest: RegisterRequest): User {
        return apiService.register(registerRequest)
    }

    override suspend fun getMe(): User {
        return apiService.getMe()
    }
}