package com.pdm.barbershop.navegation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pdm.barbershop.ui.feature.barbers.BarbersScreen
import com.pdm.barbershop.ui.feature.home.HomeScreen
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
        composable(AppDestination.Home.route) { HomeScreen() }
        composable(AppDestination.Services.route) { ServicesScreen() }
        composable(AppDestination.Barbers.route) { BarbersScreen() }
        composable(AppDestination.Schedule.route) { ScheduleScreen() }
        composable(AppDestination.Profile.route) { ProfileScreen() }
    }
}