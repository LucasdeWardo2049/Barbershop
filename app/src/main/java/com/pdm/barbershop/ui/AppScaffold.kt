package com.pdm.barbershop.ui



import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentDestination = com.pdm.barbershop.navegation.AppDestination.fromRoute(currentRoute)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = currentDestination?.title ?: "Barbershop") }
            )
        },
        bottomBar = {
            com.pdm.barbershop.ui.BottomBar(navController = navController)
        }
    ) { innerPadding ->
        com.pdm.barbershop.navegation.AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}