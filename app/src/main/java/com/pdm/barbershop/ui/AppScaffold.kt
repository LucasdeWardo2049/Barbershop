package com.pdm.barbershop.ui

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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pdm.barbershop.navegation.AppDestination
import com.pdm.barbershop.navegation.AppNavHost
import com.pdm.barbershop.navegation.Route
import com.pdm.barbershop.ui.components.CustomBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteName = backStackEntry?.destination?.route

    // Map current typed route to an AppDestination to keep labels/titles
    val currentDestination = when (currentRouteName) {
        Route.Home::class.qualifiedName -> AppDestination.Home
        Route.Services::class.qualifiedName -> AppDestination.Services
        Route.Barbers::class.qualifiedName -> AppDestination.Barbers
        Route.Schedule::class.qualifiedName -> AppDestination.Schedule
        Route.Profile::class.qualifiedName -> AppDestination.Profile
        Route.EditProfile::class.qualifiedName -> AppDestination.EditProfile
        Route.Appointments::class.qualifiedName -> AppDestination.Appointments
        Route.ComandaHistory::class.qualifiedName -> AppDestination.ComandaHistory
        Route.Notifications::class.qualifiedName -> AppDestination.Notifications
        Route.Help::class.qualifiedName -> AppDestination.Help
        Route.About::class.qualifiedName -> AppDestination.About
        else -> null
    }

    val shouldShowBars = currentDestination in AppDestination.bottom

    Scaffold(
        topBar = {
            // A TopAppBar só será exibida nas telas principais
            if (shouldShowBars) {
                CenterAlignedTopAppBar(
                    title = { Text(text = currentDestination?.title ?: "Barbershop") },
                    // NOVO: Aplica as cores do nosso tema
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            // A BottomBar só será exibida nas telas principais
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