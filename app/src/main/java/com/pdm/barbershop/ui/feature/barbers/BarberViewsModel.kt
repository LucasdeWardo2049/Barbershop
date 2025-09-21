package com.pdm.barbershop.ui.feature.barbers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.domain.model.Barber // Import Barber model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Enum para os critérios de ordenação
enum class SortOrder {
    BY_NAME,
    BY_RATING
}

class BarbersViewModel : ViewModel() {

    private val _barbers = MutableStateFlow<List<Barber>>(emptyList())
    val barbers: StateFlow<List<Barber>> = _barbers.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.BY_NAME) // Padrão: ordenar por nome
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    // Lista de exemplo de barbeiros
    private val sampleBarbers = listOf(
        Barber(id = "1", name = "Carlos Almeida", rating = 4.5, imageUrl = null),
        Barber(id = "2", name = "Bruno Rocha", rating = 4.8, imageUrl = null),
        Barber(id = "3", name = "Miguel Santos", rating = 4.2, imageUrl = null),
        Barber(id = "4", name = "Fernando Silva", rating = 5.0, imageUrl = null),
        Barber(id = "5", name = "Ricardo Pereira", rating = 4.0, imageUrl = null)
    )

    init {
        loadBarbers()
    }

    private fun loadBarbers() {
        viewModelScope.launch {
            // Inicialmente, ordena por nome (ou pelo _sortOrder.value atual)
            _barbers.value = getSortedBarbers(sampleBarbers, _sortOrder.value)
        }
    }

    fun setSortOrder(newSortOrder: SortOrder) {
        _sortOrder.value = newSortOrder
        // Atualiza a lista de barbeiros com a nova ordenação
        _barbers.update { currentBarbers ->
            // Se estivermos usando a lista de exemplo, reordenamos ela.
            // Em um cenário real, você poderia buscar do repositório já ordenado
            // ou reordenar a lista atual que já foi carregada.
            getSortedBarbers(sampleBarbers, newSortOrder)
        }
    }

    private fun getSortedBarbers(barbersToSort: List<Barber>, order: SortOrder): List<Barber> {
        return when (order) {
            SortOrder.BY_NAME -> barbersToSort.sortedBy { it.name }
            SortOrder.BY_RATING -> barbersToSort.sortedByDescending { it.rating } // Descendente para melhor avaliação primeiro
        }
    }
}
