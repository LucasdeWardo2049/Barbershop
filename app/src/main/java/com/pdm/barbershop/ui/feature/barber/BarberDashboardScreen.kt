package com.pdm.barbershop.ui.feature.barber

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdm.barbershop.domain.model.Appointment

data class Kpi(val icon: ImageVector, val value: String, val label: String)

@Composable
fun BarberDashboardScreen(
    viewModel: BarberDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Mockar KPIs por enquanto ou calcular baseado nos appointments (se possível)
    // Em uma implementação real, viria do backend ou calculado localmente
    val appointmentCount = uiState.upcomingAppointments.size
    val revenue = uiState.upcomingAppointments.sumOf { it.totalPrice ?: 0.0 }
    
    val kpis = listOf(
        Kpi(Icons.Default.AttachMoney, "R$ ${String.format("%.2f", revenue)}", "Receita Prevista"), // Baseado nos agendamentos carregados
        Kpi(Icons.Default.Event, "$appointmentCount", "Agendamentos"),
        Kpi(Icons.Default.Star, "4.8/5", "Sua Avaliação"), // Mock
        Kpi(Icons.Default.Percent, "80%", "Ocupação Hoje") // Mock
    )

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bem-vindo,",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = uiState.barberName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(240.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(kpis) { kpi ->
                        KpiCard(kpi = kpi)
                    }
                }
            }

            item {
                Text(
                    text = "Próximos Clientes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.upcomingAppointments.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                "Você não possui agendamentos próximos.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(uiState.upcomingAppointments.take(5)) { appointment -> // Mostra os 5 primeiros
                    AppointmentCard(appointment = appointment)
                }
            }
        }
    }
}

@Composable
fun KpiCard(kpi: Kpi) {
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = kpi.icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = kpi.value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = kpi.label, fontSize = 14.sp)
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = appointment.clientName, fontWeight = FontWeight.Bold)
                Text(text = appointment.serviceName, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Formatação simples da hora, idealmente usar DateTimeUtils
            val timeLabel = try {
                 appointment.startTime.substring(11, 16) // Pega HH:mm do ISO string
            } catch (e: Exception) {
                appointment.startTime
            }
            
            Text(text = timeLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
