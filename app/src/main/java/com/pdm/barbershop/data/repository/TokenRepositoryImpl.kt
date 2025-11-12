package com.pdm.barbershop.data.repository

import android.content.SharedPreferences
import com.pdm.barbershop.domain.repository.TokenRepository
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : TokenRepository {

    companion object {
        private const val KEY_TOKEN = "jwt_token"
    }

    override suspend fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    override suspend fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    override suspend fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }
}