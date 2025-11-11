package com.pdm.barbershop.ui.feature.services

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.barbershop.data.core.NetworkResult
import com.pdm.barbershop.domain.usecase.GetProductsUseCase
import com.pdm.barbershop.domain.usecase.GetServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val getServices: GetServicesUseCase,
    private val getProducts: GetProductsUseCase
) : ViewModel() {

    private val _state = mutableStateOf(ServicesUiState())
    val state: State<ServicesUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val servicesResult = getServices()
            val productsResult = getProducts()

            val services = if (servicesResult is NetworkResult.Success) servicesResult.data else emptyList()
            val products = if (productsResult is NetworkResult.Success) productsResult.data else emptyList()

            val error = if (servicesResult is NetworkResult.Error) servicesResult.message else if (productsResult is NetworkResult.Error) productsResult.message else null

            _state.value = _state.value.copy(
                isLoading = false,
                services = services,
                products = products,
                error = error
            )
        }
    }

    fun onTabSelected(tab: ServicesTab) {
        _state.value = _state.value.copy(selectedTab = tab)
    }
}
