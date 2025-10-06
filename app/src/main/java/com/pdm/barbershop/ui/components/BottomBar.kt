package com.pdm.barbershop.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pdm.barbershop.navegation.AppDestination
import com.pdm.barbershop.navegation.Route

@Composable
fun CustomBottomBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteName = navBackStackEntry?.destination?.route
    val destinations = AppDestination.bottom

    Surface(
        color = Color(0xFF1E3932),
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEach { destination ->
                val isSelected = when (destination) {
                    AppDestination.Home -> currentRouteName == Route.Home::class.qualifiedName
                    AppDestination.Services -> currentRouteName == Route.Services::class.qualifiedName
                    AppDestination.Barbers -> currentRouteName == Route.Barbers::class.qualifiedName
                    AppDestination.Schedule -> currentRouteName == Route.Schedule::class.qualifiedName
                    AppDestination.Profile -> currentRouteName == Route.Profile::class.qualifiedName
                    else -> false
                }
                BottomBarItem(
                    destination = destination,
                    isSelected = isSelected,
                    onClick = {
                        when (destination) {
                            AppDestination.Home -> navController.navigate(Route.Home)
                            AppDestination.Services -> navController.navigate(Route.Services())
                            AppDestination.Barbers -> navController.navigate(Route.Barbers)
                            AppDestination.Schedule -> navController.navigate(Route.Schedule)
                            AppDestination.Profile -> navController.navigate(Route.Profile)
                            else -> {}
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBarItem(
    destination: AppDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val cardColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF385D51) else Color.Transparent,
        animationSpec = tween(300)
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFF3D9C9) else Color(0xFFE6A685),
        animationSpec = tween(300)
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = stringResource(id = destination.labelRes),
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = destination.labelRes),
            color = contentColor,
            fontSize = 12.sp
        )
    }
}