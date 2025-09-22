package com.pdm.barbershop.navegation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
    startDestination: String = AppDestination.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AppDestination.Home.route) { HomeScreen() }
        composable(AppDestination.Services.route) { ServicesScreen() }
        composable(AppDestination.Barbers.route) { BarbersScreen() }
        composable(AppDestination.Schedule.route) { ScheduleScreen() }

        composable(AppDestination.Profile.route) {
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

