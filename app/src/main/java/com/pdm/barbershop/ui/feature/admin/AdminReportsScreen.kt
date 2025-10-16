package com.pdm.barbershop.ui.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

data class MonthlyRevenue(val month: String, val revenue: Float)
data class ServicePopularity(val serviceName: String, val percentage: Float)

@Composable
fun AdminReportsScreen() {
    val revenueData = listOf(
        MonthlyRevenue("Jan", 12000f),
        MonthlyRevenue("Fev", 15500f),
        MonthlyRevenue("Mar", 13200f),
        MonthlyRevenue("Abr", 18500f)
    )

    val serviceData = listOf(
        ServicePopularity("Corte Degradê", 0.45f),
        ServicePopularity("Barba Terapia", 0.30f),
        ServicePopularity("Sobrancelha", 0.15f),
        ServicePopularity("Outros", 0.10f)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Relatórios Globais",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            RevenueReportCard(data = revenueData)
        }

        item {
            ServicePopularityCard(data = serviceData)
        }
    }
}

@Composable
fun RevenueReportCard(data: List<MonthlyRevenue>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Receita por Mês", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            data.forEach { item ->
                BarChartItem(label = item.month, value = item.revenue, maxValue = 20000f)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BarChartItem(label: String, value: Float, maxValue: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, modifier = Modifier.width(40.dp))
        LinearProgressIndicator(
            progress = { value / maxValue },
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Text(
            text = "R$ ${value.toInt()}",
            modifier = Modifier.padding(start = 8.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ServicePopularityCard(data: List<ServicePopularity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Serviços Mais Populares", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            data.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${item.serviceName} (${(item.percentage * 100).toInt()}%)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
