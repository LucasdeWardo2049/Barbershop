package com.pdm.barbershop.ui.feature.appointments

import androidx.lifecycle.ViewModel
import com.pdm.barbershop.domain.model.Appointment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.pdm.barbershop.data.repository.AppointmentsRepository
import com.pdm.barbershop.domain.repository.UserRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppointmentsUiState(
    val appointments: List<Appointment> = emptyList()
)

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val appointmentsRepository: AppointmentsRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchAppointments()
    }

    private fun fetchAppointments() {
        viewModelScope.launch {
            val userId = userRepository.currentUser.value?.userId?.toLongOrNull()
            if (userId == null) {
                _uiState.value = AppointmentsUiState(emptyList())
                return@launch
            }
            try {
                val list = appointmentsRepository.listByClient(userId)
                _uiState.value = AppointmentsUiState(list)
            } catch (e: Exception) {
                // fallback empty; could expose error state later
                _uiState.value = AppointmentsUiState(emptyList())
            }
        }
    }
}