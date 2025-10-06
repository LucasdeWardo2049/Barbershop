package com.pdm.barbershop.navegation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        // startDestination typed
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToSchedule = { navController.navigate(Route.Schedule) },
                onNavigateToServices = { navController.navigate(Route.Services()) },
                onNavigateToBarbers = { navController.navigate(Route.Barbers) },
                onNavigateToAppointmentDetails = { _ ->
                    navController.navigate(Route.Appointments)
                }
            )
        }

        composable<Route.Services> { backStackEntry ->
            // Optional: access typed filters from URL/back stack
            val args = backStackEntry.toRoute<Route.Services>()
            // ...use args to setup state if desired
            ServicesScreen()
        }

        composable<Route.ServiceDetail>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "barbershop://service/{serviceId}" }
            )
        ) { entry ->
            val args = entry.toRoute<Route.ServiceDetail>()
            com.pdm.barbershop.ui.feature.services.ServiceDetailScreen(
                serviceId = args.serviceId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Barbers> { BarbersScreen() }
        composable<Route.Schedule> { ScheduleScreen() }

        composable<Route.Profile> {
            ProfileScreen(
                onEditProfileClick = { navController.navigate(Route.EditProfile) },
                onAppointmentsClick = { navController.navigate(Route.Appointments) },
                onComandasClick = { navController.navigate(Route.ComandaHistory) },
                onNotificationsClick = { navController.navigate(Route.Notifications) },
                onHelpClick = { navController.navigate(Route.Help) },
                onAboutClick = { navController.navigate(Route.About) }
            )
        }

        composable<Route.EditProfile> {
            EditProfileScreen(
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable<Route.Appointments> { AppointmentsScreen(onBackClick = { navController.popBackStack() }) }
        composable<Route.ComandaHistory> { ComandaHistoryScreen(onBackClick = { navController.popBackStack() }) }
        composable<Route.Notifications> { NotificationsScreen(onBackClick = { navController.popBackStack() }) }
        composable<Route.Help> { HelpScreen(onBackClick = { navController.popBackStack() }) }
        composable<Route.About> { AboutScreen(onBackClick = { navController.popBackStack() }) }
    }
}
