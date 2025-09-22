package com.pdm.barbershop.navegation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.pdm.barbershop.R

sealed class AppDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val title: String,
) {
    data object Home : AppDestination("home", R.string.nav_home, Icons.Outlined.Home, "Início")
    data object Services : AppDestination("services", R.string.nav_services, Icons.Outlined.ContentCut, "Serviços")
    data object Barbers : AppDestination("barbers", R.string.nav_barbers, Icons.Outlined.Group, "Barbeiros")
    data object Schedule : AppDestination("schedule", R.string.nav_schedule, Icons.Outlined.CalendarMonth, "Agenda")
    data object Profile : AppDestination("profile", R.string.nav_profile, Icons.Outlined.AccountCircle, "Perfil")
    data object EditProfile : AppDestination("edit_profile", R.string.nav_edit_profile, Icons.Default.Edit, "Editar Perfil")
    data object Appointments : AppDestination("appointments", R.string.nav_appointments, Icons.Outlined.DateRange, "Meus Agendamentos")
    data object ComandaHistory : AppDestination("comanda_history", R.string.nav_comanda_history, Icons.Outlined.ReceiptLong, "Histórico de Comandas")
    data object Notifications : AppDestination("notifications", R.string.nav_notifications, Icons.Outlined.Notifications, "Notificações")
    data object Help : AppDestination("help", R.string.nav_help, Icons.Outlined.HelpOutline, "Ajuda e Suporte")
    data object About : AppDestination("about", R.string.nav_about, Icons.Outlined.Info, "Sobre o App")


    companion object {
        private val allDestinations = listOf(Home, Services, Barbers, Schedule, Profile, EditProfile, Appointments, ComandaHistory, Notifications, Help, About)
        val bottom = listOf(Home, Services, Barbers, Schedule, Profile)

        fun fromRoute(route: String?): AppDestination? =
            allDestinations.firstOrNull { it.route == route }
    }
}

