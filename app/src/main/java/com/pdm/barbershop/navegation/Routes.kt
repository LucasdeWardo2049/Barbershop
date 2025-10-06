package com.pdm.barbershop.navegation

import kotlinx.serialization.Serializable
import com.pdm.barbershop.ui.feature.services.ServicesTab


sealed interface Route {
    @Serializable
    data object Home : Route


    @Serializable
    data class Services(
        val query: String? = null,
        val minPrice: Double? = null,
        val maxPrice: Double? = null,
        val tab: ServicesTab = ServicesTab.Services
    ) : Route

    @Serializable
    data class ServiceDetail(val serviceId: String) : Route

    @Serializable
    data object Barbers : Route

    @Serializable
    data object Schedule : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data object EditProfile : Route

    @Serializable
    data object Appointments : Route

    @Serializable
    data object ComandaHistory : Route

    @Serializable
    data object Notifications : Route

    @Serializable
    data object Help : Route

    @Serializable
    data object About : Route
}
