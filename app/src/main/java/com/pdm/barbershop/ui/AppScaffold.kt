package com.pdm.barbershop.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pdm.barbershop.domain.model.UserRole
import com.pdm.barbershop.navegation.AppDestination
import com.pdm.barbershop.navegation.AppNavHost
import com.pdm.barbershop.ui.components.CustomBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    mainViewModel: MainViewModel = viewModel()
) {
    val navController = rememberNavController()
    val mainUiState by mainViewModel.uiState.collectAsState()
    val userRole = mainUiState.userRole

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Bottom nav items só quando o usuário está autenticado
    val bottomNavItems = when (userRole) {
        UserRole.CLIENT -> AppDestination.clientBottomNav
        UserRole.BARBER -> AppDestination.barberBottomNav
        UserRole.ADMIN -> AppDestination.adminBottomNav
        null -> emptyList()
    }

    val currentDestination = AppDestination.fromRoute(currentRoute)
    val shouldShowBars = currentDestination in bottomNavItems

    val startDestination = if (userRole == null) AppDestination.Login.route else AppDestination.Home.route

    Scaffold(
        topBar = {
            if (shouldShowBars) {
                CenterAlignedTopAppBar(
                    title = { Text(text = currentDestination?.title ?: "") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (shouldShowBars) {
                CustomBottomBar(
                    navController = navController,
                    bottomNavItems = bottomNavItems
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = startDestination,
            onLoginSuccess = mainViewModel::onLoginSuccess,
            onLogout = mainViewModel::onLogout
        )
    }
}
