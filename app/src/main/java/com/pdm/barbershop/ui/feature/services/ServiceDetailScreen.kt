package com.pdm.barbershop.ui.feature.services

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Detalhes do serviço: $serviceId",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Aqui você pode apresentar as informações do serviço, preço, duração, etc.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(onClick = onBack, modifier = Modifier.padding(top = 24.dp)) {
            Text("Voltar")
        }
    }
}

