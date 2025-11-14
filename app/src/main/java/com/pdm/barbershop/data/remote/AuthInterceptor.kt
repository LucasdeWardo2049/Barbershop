package com.pdm.barbershop.data.remote

import android.util.Log
import com.pdm.barbershop.data.session.SessionManager
import com.pdm.barbershop.domain.repository.TokenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token = runBlocking { tokenRepository.getToken() }
        if (token.isNullOrBlank()) {
            Log.d("AuthInterceptor", "No token available - skipping Authorization header")
        } else {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            // Log only a safe prefix of the token to avoid leaking secrets
            val prefix = token.take(12)
            Log.d("AuthInterceptor", "Authorization header set (prefix=$prefix...)")
        }

        val response = chain.proceed(requestBuilder.build())
        if (response.code in listOf(401, 403)) {
            CoroutineScope(Dispatchers.Default).launch { sessionManager.notifyExpired() }
        }
        return response
    }
}