package com.pdm.barbershop.ui.feature.login

import com.pdm.barbershop.domain.model.UserRole

sealed interface LoginEvent {
    data class NavigateTo(val role: UserRole) : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}
