package com.pdm.barbershop.ui.feature.register

sealed class RegisterEvent {
    data object NavigateToLogin : RegisterEvent()
    data class ShowError(val message: String) : RegisterEvent()
}
