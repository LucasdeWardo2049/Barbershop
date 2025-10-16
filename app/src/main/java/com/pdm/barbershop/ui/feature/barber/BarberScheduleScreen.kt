package com.pdm.barbershop.ui.feature.barber

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class BarberAppointment(val time: String, val clientName: String, val service: String, val price: Double)

@Composable
fun BarberScheduleScreen() {
    val todayAppointments = listOf(
        BarberAppointment("09:00", "Carlos Silva", "Corte Masculino", 50.0),
        BarberAppointment("10:00", "Mariana Costa", "Corte e Barba", 90.0),
        BarberAppointment("11:30", "Pedro Almeida", "Barba", 40.0),
        BarberAppointment("14:00", "Gabriel Becil", "Corte Degradê", 50.0),
        BarberAppointment("15:00", "André Guedes", "Barba e Corte", 90.0),
        BarberAppointment("16:30", "Renato Lima", "Sobrancelha", 20.0),
        BarberAppointment("17:30", "Lucas Martins", "Corte Infantil", 45.0)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Agenda de Hoje",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(todayAppointments) { appointment ->
            ScheduleItemCard(appointment = appointment)
        }
    }
}

@Composable
fun ScheduleItemCard(appointment: BarberAppointment) {
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
            // Coluna do Horário
            Text(
                text = appointment.time,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            // Coluna do Cliente e Serviço
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = appointment.clientName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = appointment.service,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Coluna do Preço
            Text(
                text = "R$ ${String.format("%.2f", appointment.price)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
