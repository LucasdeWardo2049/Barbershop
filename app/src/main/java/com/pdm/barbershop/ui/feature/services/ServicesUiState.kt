package com.pdm.barbershop.ui.feature.services

import com.pdm.barbershop.domain.model.CatalogItem

enum class ServicesTab { Services, Products }

// Sealed UI state to clearly represent loading, success, and error
sealed interface ServicesUiState {
    data object Loading : ServicesUiState
    data class Content(
        val selectedTab: ServicesTab = ServicesTab.Services,
        val services: List<CatalogItem> = emptyList(),
        val products: List<CatalogItem> = emptyList()
    ) : ServicesUiState
    data class Error(val message: String) : ServicesUiState
}
