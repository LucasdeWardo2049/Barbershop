package com.pdm.barbershop.navegation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pdm.barbershop.domain.model.UserRole
import com.pdm.barbershop.ui.feature.about.AboutScreen
import com.pdm.barbershop.ui.feature.admin.AdminDashboardScreen
import com.pdm.barbershop.ui.feature.admin.AdminReportsScreen
import com.pdm.barbershop.ui.feature.admin.AdminUsersScreen
import com.pdm.barbershop.ui.feature.appointments.AppointmentsScreen
import com.pdm.barbershop.ui.feature.barber.BarberDashboardScreen
import com.pdm.barbershop.ui.feature.barber.BarberReportsScreen
import com.pdm.barbershop.ui.feature.barber.BarberScheduleScreen
import com.pdm.barbershop.ui.feature.barbers.BarbersScreen
import com.pdm.barbershop.ui.feature.comanda.ComandaHistoryScreen
import com.pdm.barbershop.ui.feature.help.HelpScreen
import com.pdm.barbershop.ui.feature.home.HomeScreen
import com.pdm.barbershop.ui.feature.login.LoginScreen
import com.pdm.barbershop.ui.feature.notifications.NotificationsScreen
import com.pdm.barbershop.ui.feature.profile.EditProfileScreen
import com.pdm.barbershop.ui.feature.profile.ProfileScreen
import com.pdm.barbershop.ui.feature.schedule.ScheduleScreen
import com.pdm.barbershop.ui.feature.services.ServicesScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLoginSuccess: (UserRole) -> Unit,
    onLogout: () -> Unit,
    startDestination: String = AppDestination.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AppDestination.Login.route) {
            LoginScreen(onNavigate = { userRole ->
                val route = when (userRole) {
                    UserRole.CLIENT -> AppDestination.Home.route
                    UserRole.BARBER -> AppDestination.BarberDashboard.route
                    UserRole.ADMIN -> AppDestination.AdminDashboard.route
                }
                onLoginSuccess(userRole) // Informa ao MainViewModel sobre o login
                navController.navigate(route) {
                    popUpTo(AppDestination.Login.route) { inclusive = true }
                }
            })
        }

        // Client Flow
        composable(AppDestination.Home.route) {
            HomeScreen(
                onNavigateToSchedule = { navController.navigate(AppDestination.Schedule.route) },
                onNavigateToServices = { navController.navigate(AppDestination.Services.route) },
                onNavigateToBarbers = { navController.navigate(AppDestination.Barbers.route) },
                onNavigateToAppointmentDetails = { navController.navigate(AppDestination.Appointments.route) }
            )
        }
        composable(AppDestination.Services.route) { ServicesScreen() }
        composable(AppDestination.Barbers.route) { BarbersScreen() }
        composable(AppDestination.Schedule.route) { ScheduleScreen() }
        composable(AppDestination.Appointments.route) { AppointmentsScreen(onBackClick = { navController.popBackStack() }) }
        composable(AppDestination.ComandaHistory.route) { ComandaHistoryScreen(onBackClick = { navController.popBackStack() }) }

        // Barber Flow
        composable(AppDestination.BarberDashboard.route) { BarberDashboardScreen() }
        composable(AppDestination.BarberSchedule.route) { BarberScheduleScreen() }
        composable(AppDestination.BarberReports.route) { BarberReportsScreen() }

        // Admin Flow
        composable(AppDestination.AdminDashboard.route) { AdminDashboardScreen() }
        composable(AppDestination.AdminUsers.route) { AdminUsersScreen() }
        composable(AppDestination.AdminReports.route) { AdminReportsScreen() }

        // Common Reusable Screens
        composable(AppDestination.Profile.route) {
            ProfileScreen(
                onEditProfileClick = { navController.navigate(AppDestination.EditProfile.route) },
                onAppointmentsClick = { navController.navigate(AppDestination.Appointments.route) },
                onComandasClick = { navController.navigate(AppDestination.ComandaHistory.route) },
                onNotificationsClick = { navController.navigate(AppDestination.Notifications.route) },
                onHelpClick = { navController.navigate(AppDestination.Help.route) },
                onAboutClick = { navController.navigate(AppDestination.About.route) },
                onLogoutClick = {
                    onLogout() // Informa  MainViewModel sobre o logout
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(AppDestination.EditProfile.route) {
            EditProfileScreen(
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(AppDestination.Notifications.route) {
            NotificationsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(AppDestination.Help.route) {
            HelpScreen(onBackClick = { navController.popBackStack() })
        }
        composable(AppDestination.About.route) {
            AboutScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
