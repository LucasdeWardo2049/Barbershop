package com.pdm.barbershop.data.repository

import android.util.Log
import com.pdm.barbershop.domain.model.User
import com.pdm.barbershop.domain.repository.AuthRepository
import com.pdm.barbershop.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository
) : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun fetchUser() {
        try {
            Log.d("UserRepository", "Buscando dados do usuário...")
            val user = authRepository.getMe()
            _currentUser.value = user
            Log.d("UserRepository", "Usuário buscado com sucesso: ${user.name}")
        } catch (e: Exception) {
            // Em caso de erro, garante que o usuário seja nulo
            Log.e("UserRepository", "Falha ao buscar usuário", e)
            _currentUser.value = null
        }
    }

    override suspend fun clearUser() {
        _currentUser.value = null
    }
}