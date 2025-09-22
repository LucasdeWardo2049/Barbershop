package com.pdm.barbershop.ui.feature.comanda

import androidx.lifecycle.ViewModel
import com.pdm.barbershop.domain.model.Comanda
import com.pdm.barbershop.domain.model.ComandaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ComandaHistoryUiState(
    val comandas: List<Comanda> = emptyList()
)

class ComandaHistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ComandaHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchComandaHistory()
    }

    private fun fetchComandaHistory() {
        // Dados Mockados
        _uiState.value = ComandaHistoryUiState(
            comandas = listOf(
                Comanda("1", "18/09/2025", 85.00, listOf(
                    ComandaItem("Barba Terapia", 50.00),
                    ComandaItem("Pomada Modeladora", 35.00)
                )),
                Comanda("2", "12/09/2025", 90.00, listOf(
                    ComandaItem("Corte e Barba", 90.00)
                )),
                Comanda("3", "05/09/2025", 120.00, listOf(
                    ComandaItem("Pintura Capilar", 120.00)
                ))
            )
        )
    }
}