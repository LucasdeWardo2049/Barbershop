package com.pdm.barbershop.data.remote

import com.pdm.barbershop.domain.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // runBlocking é usado aqui porque o interceptador não é uma função suspend.
        // Esta é uma das poucas exceções onde o uso de runBlocking é aceitável.
        val token = runBlocking {
            tokenRepository.getToken()
        }

        // Log para depuração
        println("AuthInterceptor: Token -> $token")

        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}