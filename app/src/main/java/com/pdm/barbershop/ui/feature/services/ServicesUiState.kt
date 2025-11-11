package com.pdm.barbershop.ui.feature.services

import com.pdm.barbershop.domain.model.CatalogItem

enum class ServicesTab { Services, Products }

// Representa os estados de UI como uma hierarquia selada, alinhada ao uso na tela e ViewModel
sealed interface ServicesUiState {
    data object Loading : ServicesUiState
    data class Content(
        val selectedTab: ServicesTab = ServicesTab.Services,
        val services: List<CatalogItem> = emptyList(),
        val products: List<CatalogItem> = emptyList()
    ) : ServicesUiState
    data class Error(val message: String) : ServicesUiState
}
