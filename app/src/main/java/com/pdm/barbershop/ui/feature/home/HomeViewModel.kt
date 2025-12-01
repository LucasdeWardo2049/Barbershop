package com.pdm.barbershop.ui.feature.home

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.remote.ApiService
import com.pdm.barbershop.data.repository.AppointmentsRepository
import com.pdm.barbershop.data.session.SessionManager
import com.pdm.barbershop.domain.model.Appointment
import com.pdm.barbershop.domain.model.Service
import com.pdm.barbershop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.OffsetDateTime

data class HomeUiState(
    val userName: String = "",
    val userAvatar: Any? = null,
    val nextAppointment: Appointment? = null,
    val lastServiceForRebooking: Service? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val sessionExpired: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appointmentsRepository: AppointmentsRepository,
    private val sessionManager: SessionManager,
    private val apiService: ApiService,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeUser()
        observeSession()
        loadRemainingData()
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                _uiState.update { 
                    it.copy(userName = user?.name ?: "") 
                }
                if (user != null) {
                    fetchAppointmentsForHome(user.userId)
                    fetchUserAvatar(user.userId, user.avatarUrl)
                }
            }
        }
    }

    private fun fetchUserAvatar(userId: String, avatarUrl: String?) {
        if (avatarUrl == null) {
             _uiState.update { it.copy(userAvatar = null) }
             return
        }

        viewModelScope.launch {
            try {
                val responseBody = apiService.getAvatar(userId)
                val tempFile = File(application.cacheDir, "avatar_${userId}_home.jpg")
                responseBody.byteStream().use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                _uiState.update { it.copy(userAvatar = tempFile.toUri()) }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching avatar", e)
                _uiState.update { it.copy(userAvatar = null) } 
            }
        }
    }

    private fun observeSession() {
        viewModelScope.launch {
            sessionManager.sessionExpired.collectLatest {
                userRepository.clearUser()
                _uiState.update { it.copy(sessionExpired = true, error = "Sessão expirada. Faça login novamente.") }
            }
        }
    }

    fun consumeSessionExpired() { _uiState.update { it.copy(sessionExpired = false) } }

    @Suppress("NewApi")
    private fun fetchAppointmentsForHome(userId: String) {
        viewModelScope.launch {
            try {
                val list = appointmentsRepository.listMyAppointments()
                
                // Ordenar os agendamentos por data (assumindo formato ISO 8601)
                val sortedList = list.sortedBy { appointment ->
                    OffsetDateTime.parse(appointment.startTime)
                }

                // Filtrar apenas agendamentos futuros e NÃO cancelados
                val now = OffsetDateTime.now()
                val futureAppointments = sortedList.filter { appointment ->
                    val isFuture = OffsetDateTime.parse(appointment.startTime).isAfter(now)
                    val isNotCancelled = !appointment.status.equals("CANCELLED", ignoreCase = true)
                    isFuture && isNotCancelled
                }

                val next = futureAppointments.firstOrNull()
                
                // Para rebooking, pegamos o último serviço realizado (pode ser do passado)
                // Se não houver passado, pega o último da lista geral
                val lastServiceAppointment = list.lastOrNull()
                
                val lastService = lastServiceAppointment?.let { appt ->
                    Service(
                        id = appt.serviceId.toString(),
                        name = appt.serviceName,
                        price = 0.0, // Preço pode não estar disponível no objeto Appointment simplificado
                        durationInMinutes = 30,
                        icon = Icons.Filled.ContentCut
                    )
                }
                
                _uiState.update { it.copy(
                    nextAppointment = next, 
                    lastServiceForRebooking = lastService, 
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun loadRemainingData() {
        _uiState.update { it.copy(isLoading = true) }
    }
}
