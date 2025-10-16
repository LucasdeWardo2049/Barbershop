package com.pdm.barbershop.ui.feature.services

import com.pdm.barbershop.domain.model.CatalogItem

enum class ServicesTab { Services, Products }

data class ServicesUiState(
    val isLoading: Boolean = true,
    val selectedTab: ServicesTab = ServicesTab.Services,
    val services: List<CatalogItem> = emptyList(),
    val products: List<CatalogItem> = emptyList(),
    val error: String? = null
)

