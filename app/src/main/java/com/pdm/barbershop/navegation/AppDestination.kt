package com.pdm.barbershop.navegation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
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

    companion object {
        val bottom = listOf(Home, Services, Barbers, Schedule, Profile)

        fun fromRoute(route: String?): AppDestination? =
            bottom.firstOrNull { it.route == route }
    }
}