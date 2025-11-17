package com.pdm.barbershop.data.repository

import com.pdm.barbershop.data.remote.AuthApiService
import com.pdm.barbershop.data.remote.AuthRequest
import com.pdm.barbershop.data.remote.AuthResponse
import com.pdm.barbershop.data.remote.RegisterRequest
import com.pdm.barbershop.data.remote.UpdateUserRequest
import com.pdm.barbershop.data.remote.dto.UserDto
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
        return apiService.register(registerRequest).toDomain()
    }

    override suspend fun getMe(): User {
        return apiService.getMe().toDomain()
    }

    override suspend fun updateUser(userId: String, request: UpdateUserRequest): User {
        return apiService.updateUser(userId, request).toDomain()
    }

    private fun UserDto.toDomain(): User = User(
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