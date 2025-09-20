package com.pdm.barbershop.ui.feature.notifications

import androidx.lifecycle.ViewModel
import com.pdm.barbershop.domain.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList()
)

class NotificationsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        _uiState.value = NotificationsUiState(
            notifications = listOf(
                Notification("1", "Agendamento Confirmado!", "Seu corte com Renato Lima foi confirmado para 25/09 às 10:00.", "Hoje, 09:30", false),
                Notification("2", "Lembrete de Agendamento", "Não se esqueça do seu horário amanhã às 15:30.", "Ontem, 18:00", true),
                Notification("3", "Promoção Especial", "Nesta semana, o combo Corte + Barba está com 20% de desconto.", "2 dias atrás", true),
                Notification("4", "Pagamento Recebido", "Sua comanda de R$ 90,00 foi paga com sucesso.", "5 dias atrás", true)
            )
        )
    }
}
