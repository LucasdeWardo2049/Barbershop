package com.pdm.barbershop.ui.feature.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.remote.UpdateUserRequest
import com.pdm.barbershop.domain.repository.AuthRepository
import com.pdm.barbershop.domain.repository.NotificationRepository
import com.pdm.barbershop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditProfileEvent {
    data object SaveSuccess : EditProfileEvent()
    data class ShowError(val message: String) : EditProfileEvent()
}

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<EditProfileEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val TAG = "EditProfileViewModel"

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = userRepository.currentUser.first()
            _uiState.update {
                it.copy(
                    name = user?.name ?: "",
                    email = user?.email ?: "",
                    phone = user?.phone ?: ""
                )
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onPhoneChange(newPhone: String) {
        _uiState.update { it.copy(phone = newPhone) }
    }

    fun saveChanges() {
        viewModelScope.launch {
            Log.d(TAG, "saveChanges() foi chamado.")
            _uiState.update { it.copy(isLoading = true) }

            val currentUser = userRepository.currentUser.value
            Log.d(TAG, "Verificando currentUser: $currentUser")

            if (currentUser == null) {
                Log.e(TAG, "Erro Crítico: currentUser está nulo. A atualização não pode prosseguir.")
                _eventChannel.send(EditProfileEvent.ShowError("Usuário não encontrado. Tente novamente."))
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            try {
                val request = UpdateUserRequest(
                    name = uiState.value.name.trim(),
                    email = uiState.value.email.trim(),
                    phone = uiState.value.phone.trim(),
                    role = currentUser.role
                )
                Log.d(TAG, "Enviando requisição de atualização para o usuário ${currentUser.userId} com o corpo: $request")

                authRepository.updateUser(currentUser.userId, request)
                Log.d(TAG, "Update bem-sucedido na API. Buscando usuário atualizado...")

                // Força a atualização do cache e espera a conclusão
                val fetchJob: Job = launch { userRepository.fetchUser() }
                fetchJob.join() 

                // Adiciona notificação
                notificationRepository.addNotification(
                    title = "Perfil Atualizado",
                    message = "Seus dados pessoais foram alterados com sucesso."
                )

                Log.d(TAG, "Cache do usuário atualizado. Enviando evento de sucesso.")
                _eventChannel.send(EditProfileEvent.SaveSuccess)

            } catch (e: Exception) {
                Log.e(TAG, "Falha ao salvar alterações.", e)
                _eventChannel.send(EditProfileEvent.ShowError(e.message ?: "Erro desconhecido ao salvar"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
