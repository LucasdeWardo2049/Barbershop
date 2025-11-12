package com.pdm.barbershop.ui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdm.barbershop.domain.model.Appointment
import com.pdm.barbershop.domain.model.Service
import com.pdm.barbershop.domain.repository.AuthRepository

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSchedule: () -> Unit = {},
    onNavigateToServices: () -> Unit = {},
    onNavigateToBarbers: () -> Unit = {},
    onNavigateToAppointmentDetails: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 1. Saudação
        GreetingSection(userName = uiState.userName)

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Card do Próximo Agendamento
        NextAppointmentCard(
            appointment = uiState.nextAppointment,
            onSeeDetailsClicked = { appointmentId ->
                onNavigateToAppointmentDetails(appointmentId)
            },
            onScheduleClicked = onNavigateToSchedule
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Botões de Atalho
        QuickActionsSection(
            onScheduleServiceClicked = onNavigateToSchedule,
            onServicesClicked = onNavigateToServices,
            onBarbersClicked = onNavigateToBarbers
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Card de Reagendamento Rápido
        QuickRebookCard(
            service = uiState.lastServiceForRebooking,
            onRebookClicked = onNavigateToSchedule
        )
    }
}

@Composable
fun GreetingSection(userName: String) {
    val name = if (userName.isNotBlank()) userName else "Visitante"
    Text(
        text = "Olá, $name",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun NextAppointmentCard(
    appointment: Appointment?,
    onSeeDetailsClicked: (String) -> Unit,
    onScheduleClicked: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Próximo Agendamento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (appointment != null) {
                Text(
                    text = "${appointment.serviceName} - ${appointment.date} às ${appointment.time}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Com ${appointment.barberName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onSeeDetailsClicked(appointment.id) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Ver Detalhes")
                }
            } else {
                Text(
                    text = "Você não possui agendamentos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onScheduleClicked,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Agendar um horário")
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onScheduleServiceClicked: () -> Unit,
    onServicesClicked: () -> Unit,
    onBarbersClicked: () -> Unit
) {
    Column {
        Text(
            text = "Acesso Rápido",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuickActionButton(
                text = "Agendar",
                icon = Icons.Filled.CalendarMonth,
                onClick = onScheduleServiceClicked,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(8.dp))
            QuickActionButton(
                text = "Serviços",
                icon = Icons.Filled.ContentCut,
                onClick = onServicesClicked,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(8.dp))
            QuickActionButton(
                text = "Barbeiros",
                icon = Icons.Filled.Groups,
                onClick = onBarbersClicked,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, fontSize = 12.sp)
        }
    }
}

@Composable
fun QuickRebookCard(service: Service?, onRebookClicked: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Reagendamento Rápido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (service != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = service.icon,
                        contentDescription = service.name,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRebookClicked,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Filled.Repeat, contentDescription = "Reagendar", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Agendar Novamente")
                }
            } else {
                Text(
                    text = "Nenhum serviço recente para reagendar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
