package com.pdm.barbershop.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pdm.barbershop.navegation.AppDestination
import com.pdm.barbershop.navegation.AppNavHost
import com.pdm.barbershop.navegation.Route
import com.pdm.barbershop.ui.components.CustomBottomBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    // Typed routes used by BottomBar
    val home = Route.Home::class.qualifiedName
    val services = Route.Services::class.qualifiedName
    val barbers = Route.Barbers::class.qualifiedName
    val schedule = Route.Schedule::class.qualifiedName
    val profile = Route.Profile::class.qualifiedName

    // Show Top/Bottom bars when any main route is present in the hierarchy
    val shouldShowBars = destination?.hierarchy?.any { dest ->
        val r = dest.route
        r == home || r == services || r == barbers || r == schedule || r == profile
    } == true

    // Map current title from hierarchy
    val currentDestination = when {
        destination?.hierarchy?.any { it.route == home } == true -> AppDestination.Home
        destination?.hierarchy?.any { it.route == services } == true -> AppDestination.Services
        destination?.hierarchy?.any { it.route == barbers } == true -> AppDestination.Barbers
        destination?.hierarchy?.any { it.route == schedule } == true -> AppDestination.Schedule
        destination?.hierarchy?.any { it.route == profile } == true -> AppDestination.Profile
        destination?.hierarchy?.any { it.route == Route.EditProfile::class.qualifiedName } == true -> AppDestination.EditProfile
        destination?.hierarchy?.any { it.route == Route.Appointments::class.qualifiedName } == true -> AppDestination.Appointments
        destination?.hierarchy?.any { it.route == Route.ComandaHistory::class.qualifiedName } == true -> AppDestination.ComandaHistory
        destination?.hierarchy?.any { it.route == Route.Notifications::class.qualifiedName } == true -> AppDestination.Notifications
        destination?.hierarchy?.any { it.route == Route.Help::class.qualifiedName } == true -> AppDestination.Help
        destination?.hierarchy?.any { it.route == Route.About::class.qualifiedName } == true -> AppDestination.About
        else -> null
    }

    Scaffold(
        topBar = {
            if (shouldShowBars) {
                CenterAlignedTopAppBar(
                    title = { Text(text = currentDestination?.title ?: "Barbershop") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (shouldShowBars) {
                CustomBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}