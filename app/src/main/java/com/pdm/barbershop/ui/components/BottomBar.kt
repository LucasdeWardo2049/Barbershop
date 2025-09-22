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

@Composable
fun CustomBottomBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val destinations = AppDestination.bottom

    Surface(
        color = Color(0xFF1E3932), // Cor de fundo da barra (dark_green)
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // <<< ADICIONADO AQUI
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Um pouco mais alto para um visual premium
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEach { destination ->
                BottomBarItem(
                    destination = destination,
                    isSelected = currentRoute == destination.route,
                    onClick = {
                        if (currentRoute != destination.route) {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
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

    // Animação de cores para o card e para o conteúdo (ícone e texto)
    val cardColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF385D51) else Color.Transparent, // medium_green para selecionado
        animationSpec = tween(300)
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFF3D9C9) else Color(0xFFE6A685), // light_peach para selecionado, dark_peach para não selecionado
        animationSpec = tween(300)
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Remove o efeito de ripple padrão
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