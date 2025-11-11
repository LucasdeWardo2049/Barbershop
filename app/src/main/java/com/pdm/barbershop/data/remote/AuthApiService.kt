package com.pdm.barbershop.data.remote

import com.pdm.barbershop.domain.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): User
}