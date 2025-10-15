package com.pdm.barbershop.ui

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pdm.barbershop.navegation.AppDestination
import com.pdm.barbershop.navegation.AppNavHost
import com.pdm.barbershop.ui.components.CustomBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()

    // Handle initial deep link intent
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val activity = context as? Activity
        val intent = activity?.intent
        if (intent != null) {
            navController.handleDeepLink(intent)
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentDestination = AppDestination.fromRoute(currentRoute)

    // NOVO: Lógica para decidir quando exibir as barras de navegação
    val shouldShowBars = currentDestination in AppDestination.bottom

    Scaffold(
        topBar = {
            // A TopAppBar só será exibida nas telas principais
            if (shouldShowBars) {
                CenterAlignedTopAppBar(
                    title = { Text(text = currentDestination?.title ?: "Barbershop") },
                    // NOVO: Aplica as cores do nosso tema
                    colors = TopAppBarDefaults.topAppBarColors(
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