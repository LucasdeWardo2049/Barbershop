package com.pdm.barbershop.ui.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pdm.barbershop.domain.model.UserRole

data class MockUser(val name: String, val email: String, val role: UserRole)

@Composable
fun AdminUsersScreen() {
    val users = listOf(
        MockUser("Carlos Silva", "carlos@cliente.com", UserRole.CLIENT),
        MockUser("Mariana Costa", "mariana@cliente.com", UserRole.CLIENT),
        MockUser("Gabriel Becil (Barbearia do Gabriel)", "gabriel@barbeiro.com", UserRole.BARBER),
        MockUser("Renato Lima (Barbearia do Renato)", "renato@barbeiro.com", UserRole.BARBER),
        MockUser("Admin Master", "admin@admin.com", UserRole.ADMIN)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Gerenciamento de Usuários",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(users) { user ->
            UserItemCard(user = user)
        }
    }
}

@Composable
fun UserItemCard(user: MockUser) {
    val icon: ImageVector
    val iconDescription: String

    when (user.role) {
        UserRole.CLIENT -> {
            icon = Icons.Default.Person
            iconDescription = "Cliente"
        }
        UserRole.BARBER -> {
            icon = Icons.Default.ContentCut
            iconDescription = "Barbeiro"
        }
        UserRole.ADMIN -> {
            icon = Icons.Default.AdminPanelSettings
            iconDescription = "Administrador"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column {
                Text(text = user.name, fontWeight = FontWeight.Bold)
                Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
