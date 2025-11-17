package com.pdm.barbershop.ui.feature.appointments

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.pdm.barbershop.domain.model.Appointment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.pdm.barbershop.data.repository.AppointmentsRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

data class AppointmentsUiState(
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val appointmentsRepository: AppointmentsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchAppointments()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAppointments() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val list = appointmentsRepository.listMyAppointments()
                _uiState.update { it.copy(
                    appointments = list,
                    isLoading = false,
                    errorMessage = null
                ) }
            } catch (e: Exception) {
                val errorMsg = classifyError(e)
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                ) }
            }
        }
    }

    private fun classifyError(e: Exception): String = when (e) {
        is UnknownHostException -> "Servidor não encontrado. Verifique a conexão."
        is ConnectException -> "Não foi possível conectar ao servidor."
        is SocketTimeoutException -> "Tempo de resposta excedido. Tente novamente."
        is HttpException -> when (e.code()) {
            401, 403 -> "Sessão expirada. Faça login novamente."
            else -> "Erro ${e.code()} ao buscar agendamentos."
        }
        else -> e.message ?: "Erro ao carregar agendamentos"
    }
}