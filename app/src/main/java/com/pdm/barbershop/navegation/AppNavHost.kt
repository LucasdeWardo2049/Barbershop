package com.pdm.barbershop.navegation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.pdm.barbershop.ui.feature.about.AboutScreen
import com.pdm.barbershop.ui.feature.appointments.AppointmentsScreen
import com.pdm.barbershop.ui.feature.barbers.BarbersScreen
import com.pdm.barbershop.ui.feature.comanda.ComandaHistoryScreen
import com.pdm.barbershop.ui.feature.help.HelpScreen
import com.pdm.barbershop.ui.feature.home.HomeScreen
import com.pdm.barbershop.ui.feature.notifications.NotificationsScreen
import com.pdm.barbershop.ui.feature.profile.EditProfileScreen
import com.pdm.barbershop.ui.feature.profile.ProfileScreen
import com.pdm.barbershop.ui.feature.schedule.ScheduleScreen
import com.pdm.barbershop.ui.feature.services.ServicesScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppDestination.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = AppDestination.Home.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://barbershop.app/home" },
                navDeepLink { uriPattern = "barbershop://home" }
            )
        ) {
            HomeScreen(
                onNavigateToSchedule = { navController.navigate(AppDestination.Schedule.route) },
                onNavigateToServices = { navController.navigate(AppDestination.Services.route) },
                onNavigateToBarbers = { navController.navigate(AppDestination.Barbers.route) },
                onNavigateToAppointmentDetails = { _ ->
                    navController.navigate(AppDestination.Appointments.route)
                }
            )
        }
        composable(
            route = AppDestination.Services.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://barbershop.app/services" },
                navDeepLink { uriPattern = "barbershop://services" }
            )
        ) { ServicesScreen() }
        composable(
            route = AppDestination.Barbers.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://barbershop.app/barbers" },
                navDeepLink { uriPattern = "barbershop://barbers" }
            )
        ) { BarbersScreen() }
        composable(
            route = AppDestination.Schedule.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://barbershop.app/schedule" },
                navDeepLink { uriPattern = "barbershop://schedule" }
            )
        ) { ScheduleScreen() }

        composable(
            route = AppDestination.Profile.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://barbershop.app/profile" },
                navDeepLink { uriPattern = "barbershop://profile" }
            )
        ) {
            ProfileScreen(
                onEditProfileClick = { navController.navigate(AppDestination.EditProfile.route) },
                onAppointmentsClick = { navController.navigate(AppDestination.Appointments.route) },
                onComandasClick = { navController.navigate(AppDestination.ComandaHistory.route) },
                onNotificationsClick = { navController.navigate(AppDestination.Notifications.route) },
                onHelpClick = { navController.navigate(AppDestination.Help.route) },
                onAboutClick = { navController.navigate(AppDestination.About.route) }
            )
        }

        composable(AppDestination.EditProfile.route) {
            EditProfileScreen(
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(AppDestination.Appointments.route) {
            AppointmentsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(AppDestination.ComandaHistory.route) {
            ComandaHistoryScreen(onBackClick = { navController.popBackStack() })
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
