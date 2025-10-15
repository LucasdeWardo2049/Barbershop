package com.pdm.barbershop.ui.feature.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.repository.FakeCatalogRepository
import com.pdm.barbershop.domain.repository.CatalogRepository
import com.pdm.barbershop.domain.usecase.GetProductsUseCase
import com.pdm.barbershop.domain.usecase.GetServicesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServicesViewModel(
    repository: CatalogRepository = FakeCatalogRepository(),
    private val getServices: GetServicesUseCase = GetServicesUseCase(repository),
    private val getProducts: GetProductsUseCase = GetProductsUseCase(repository)
) : ViewModel() {

    private val _state = androidx.compose.runtime.mutableStateOf<ServicesUiState>(ServicesUiState.Loading)
    val state: androidx.compose.runtime.State<ServicesUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        _state.value = ServicesUiState.Loading
        viewModelScope.launch {
            try {
                val services = withContext(Dispatchers.Default) { getServices() }
                val products = withContext(Dispatchers.Default) { getProducts() }
                _state.value = ServicesUiState.Content(
                    selectedTab = (state.value as? ServicesUiState.Content)?.selectedTab ?: ServicesTab.Services,
                    services = services,
                    products = products
                )
            } catch (t: Throwable) {
                _state.value = ServicesUiState.Error(t.message ?: "Erro ao carregar serviços e produtos")
            }
        }
    }

    fun onTabSelected(tab: ServicesTab) {
        val current = _state.value
        if (current is ServicesUiState.Content) {
            _state.value = current.copy(selectedTab = tab)
        }
    }
}
