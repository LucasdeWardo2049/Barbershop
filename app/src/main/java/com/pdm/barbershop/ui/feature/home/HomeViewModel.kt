package com.pdm.barbershop.ui.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.domain.model.Appointment
import com.pdm.barbershop.domain.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Content(
        val userName: String = "",
        val nextAppointment: Appointment? = null,
        val lastServiceForRebooking: Service? = null
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(1000)
                val exampleUserName = "Eduardo"
                val exampleNextAppointment = Appointment(
                    id = "appt123",
                    date = "24/09",
                    time = "15:00",
                    serviceName = "Corte + Barba",
                    barberName = "Fernando Silva",
                    status = "Confirmado"
                )
                val exampleLastService = Service(
                    id = "serv456",
                    name = "Corte Degradê",
                    price = 50.0,
                    durationInMinutes = 45,
                    icon = Icons.Filled.ContentCut
                )
                _uiState.value = HomeUiState.Content(
                    userName = exampleUserName,
                    nextAppointment = exampleNextAppointment,
                    lastServiceForRebooking = exampleLastService
                )
            } catch (t: Throwable) {
                _uiState.value = HomeUiState.Error(t.message ?: "Erro ao carregar a Home")
            }
        }
    }
}
