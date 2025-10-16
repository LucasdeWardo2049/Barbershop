package com.pdm.barbershop.navegation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.pdm.barbershop.R

sealed class AppDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val title: String,
) {
    // Common
    data object Login : AppDestination("login", R.string.nav_login, Icons.Outlined.Lock, "Login")
    data object Profile : AppDestination("profile", R.string.nav_profile, Icons.Outlined.AccountCircle, "Perfil")
    data object EditProfile : AppDestination("edit_profile", R.string.nav_edit_profile, Icons.Default.Edit, "Editar Perfil")
    data object Notifications : AppDestination("notifications", R.string.nav_notifications, Icons.Outlined.Notifications, "Notificações")
    data object Help : AppDestination("help", R.string.nav_help, Icons.Outlined.HelpOutline, "Ajuda e Suporte")
    data object About : AppDestination("about", R.string.nav_about, Icons.Outlined.Info, "Sobre o App")
    data object Logout : AppDestination("logout", R.string.nav_logout, Icons.Outlined.ExitToApp, "Sair")

    // Client
    data object Home : AppDestination("home", R.string.nav_home, Icons.Outlined.Home, "Início")
    data object Services : AppDestination("services", R.string.nav_services, Icons.Outlined.ContentCut, "Serviços")
    data object Barbers : AppDestination("barbers", R.string.nav_barbers, Icons.Outlined.Group, "Barbeiros")
    data object Schedule : AppDestination("schedule", R.string.nav_schedule, Icons.Outlined.CalendarMonth, "Agenda")
    data object Appointments : AppDestination("appointments", R.string.nav_appointments, Icons.Outlined.DateRange, "Meus Agendamentos")
    data object ComandaHistory : AppDestination("comanda_history", R.string.nav_comanda_history, Icons.Outlined.ReceiptLong, "Histórico de Comandas")

    // Barber
    data object BarberDashboard : AppDestination("barber_dashboard", R.string.nav_barber_dashboard, Icons.Default.Dashboard, "Painel")
    data object BarberSchedule : AppDestination("barber_schedule", R.string.nav_barber_schedule, Icons.Default.CalendarMonth, "Agenda")
    data object BarberReports : AppDestination("barber_reports", R.string.nav_barber_reports, Icons.Default.BarChart, "Relatórios")

    // Admin
    data object AdminDashboard : AppDestination("admin_dashboard", R.string.nav_admin_dashboard, Icons.Default.AdminPanelSettings, "Painel")
    data object AdminUsers : AppDestination("admin_users", R.string.nav_admin_users, Icons.Default.People, "Usuários")
    data object AdminReports : AppDestination("admin_reports", R.string.nav_admin_reports, Icons.Default.Assessment, "Relatórios Globais")

    companion object {
        private val allDestinations = listOf(
            Login, Profile, EditProfile, Notifications, Help, About, Logout,
            Home, Services, Barbers, Schedule, Appointments, ComandaHistory,
            BarberDashboard, BarberSchedule, BarberReports,
            AdminDashboard, AdminUsers, AdminReports
        )

        val clientBottomNav = listOf(Home, Schedule, Appointments, Profile)
        val barberBottomNav = listOf(BarberDashboard, BarberSchedule, BarberReports, Profile)
        val adminBottomNav = listOf(AdminDashboard, AdminUsers, AdminReports, Profile)

        fun fromRoute(route: String?): AppDestination? =
            allDestinations.firstOrNull { it.route == route }
    }
}
