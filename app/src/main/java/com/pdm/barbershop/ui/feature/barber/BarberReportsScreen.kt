package com.pdm.barbershop.ui.feature.barber

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Modelos de dados mockados para os relatórios do barbeiro
data class DailyPerformance(val label: String, val value: String)
data class PerformedService(val name: String, val count: Int)

@Composable
fun BarberReportsScreen() {
    // 1. Dados mockados
    val dailyPerformanceData = listOf(
        DailyPerformance("Faturamento do Dia", "R$ 450,00"),
        DailyPerformance("Agendamentos Concluídos", "9"),
        DailyPerformance("Ticket Médio", "R$ 50,00"),
        DailyPerformance("Avaliação Média (Hoje)", "4.9/5")
    )

    val topServicesData = listOf(
        PerformedService("Corte Degradê", 5),
        PerformedService("Barba e Corte", 2),
        PerformedService("Sobrancelha", 2)
    )

    // 2. Layout principal rolável
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Seus Relatórios",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // --- Card de Desempenho do Dia ---
        item {
            DailyPerformanceCard(data = dailyPerformanceData)
        }

        // --- Card de Serviços Mais Realizados ---
        item {
            TopServicesCard(data = topServicesData)
        }
    }
}

// --- Composables reutilizáveis para os Relatórios ---

@Composable
fun DailyPerformanceCard(data: List<DailyPerformance>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Desempenho de Hoje", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            // Exibe os KPIs em duas colunas
            (0 until data.size step 2).forEach { index ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    KpiItem(label = data[index].label, value = data[index].value, modifier = Modifier.weight(1f))
                    if (index + 1 < data.size) {
                        KpiItem(label = data[index + 1].label, value = data[index + 1].value, modifier = Modifier.weight(1f))
                    }
                }
                if(index < data.size - 2) Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun KpiItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TopServicesCard(data: List<PerformedService>) {
    val maxCount = data.maxOfOrNull { it.count }?.toFloat() ?: 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Serviços Mais Realizados Hoje", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            data.forEach { service ->
                ServiceBarChartItem(label = service.name, value = service.count.toFloat(), maxValue = maxCount)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ServiceBarChartItem(label: String, value: Float, maxValue: Float) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodyLarge)
        LinearProgressIndicator(
            progress = { value / maxValue },
            modifier = Modifier
                .weight(0.5f)
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Text(
            text = "${value.toInt()}",
            modifier = Modifier.padding(start = 8.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}
