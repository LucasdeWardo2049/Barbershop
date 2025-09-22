package com.pdm.barbershop.ui.feature.barbers


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person // Placeholder para foto
import androidx.compose.material.icons.filled.Star // Para avaliação
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm.barbershop.domain.model.Barber

@Composable
fun BarbersScreen(viewModel: BarbersViewModel = viewModel()) {
    val barbers by viewModel.barbers.collectAsState()
    val currentSortOrder by viewModel.sortOrder.collectAsState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(barbers) {
        if (barbers.isNotEmpty()) { 
            lazyListState.scrollToItem(index = 0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isNameSelected = currentSortOrder == SortOrder.BY_NAME
            val selectedButtonContainerColor = Color(0xFFF3D9C9)
            val selectedButtonContentColor = Color(0xFF1E3932)

            Button(
                onClick = { viewModel.setSortOrder(SortOrder.BY_NAME) },
                modifier = Modifier.height(if (isNameSelected) 62.dp else 54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isNameSelected) selectedButtonContainerColor else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (isNameSelected) selectedButtonContentColor else MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Ordenar por Nome")
            }

            val isRatingSelected = currentSortOrder == SortOrder.BY_RATING
            Button(
                onClick = { viewModel.setSortOrder(SortOrder.BY_RATING) },
                modifier = Modifier.height(if (isRatingSelected) 62.dp else 54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRatingSelected) selectedButtonContainerColor else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (isRatingSelected) selectedButtonContentColor else MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Ordenar por Avaliação")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (barbers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum barbeiro encontrado.")
            }
        } else {
            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(barbers, key = { barber -> barber.id }) { barber ->
                    BarberItem(barber = barber)
                }
            }
        }
    }
}

@Composable
fun BarberItem(barber: Barber, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Person, // Placeholder
                contentDescription = "Foto do Barbeiro",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(barber.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                // Exibição da Avaliação com Estrelas
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Avaliação: ", style = MaterialTheme.typography.bodySmall)
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = if (i <= barber.rating) "Estrela Cheia" else "Estrela Vazia",
                            tint = if (i <= barber.rating.toInt()) MaterialTheme.colorScheme.primary else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(" (${barber.rating})", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BarbersScreenPreview() {
    MaterialTheme { 
        BarbersScreen(viewModel = BarbersViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun BarberItemPreview() {
    MaterialTheme {
        BarberItem(barber = Barber(id = "1", name = "Carlos Almeida", rating = 4.5))
    }
}
