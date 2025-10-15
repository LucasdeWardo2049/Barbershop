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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm.barbershop.domain.model.Appointment
import com.pdm.barbershop.domain.model.Service

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToSchedule: () -> Unit = {},
    onNavigateToServices: () -> Unit = {},
    onNavigateToBarbers: () -> Unit = {},
    onNavigateToAppointmentDetails: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val uiState = state) {
        is HomeUiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator() }
        }
        is HomeUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { Text(uiState.message) }
        }
        is HomeUiState.Content -> {
            HomeContent(
                uiState = uiState,
                onNavigateToSchedule = onNavigateToSchedule,
                onNavigateToServices = onNavigateToServices,
                onNavigateToBarbers = onNavigateToBarbers,
                onNavigateToAppointmentDetails = onNavigateToAppointmentDetails
            )
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState.Content,
    onNavigateToSchedule: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToBarbers: () -> Unit,
    onNavigateToAppointmentDetails: (String) -> Unit
) {
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
        uiState.nextAppointment?.let { appointment ->
            NextAppointmentCard(
                appointment = appointment,
                onSeeDetailsClicked = { onNavigateToAppointmentDetails(appointment.id) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. Botões de Atalho
        QuickActionsSection(
            onScheduleServiceClicked = onNavigateToSchedule,
            onServicesClicked = onNavigateToServices,
            onBarbersClicked = onNavigateToBarbers
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Card de Reagendamento Rápido
        uiState.lastServiceForRebooking?.let { service ->
            QuickRebookCard(
                service = service,
                onRebookClicked = { onNavigateToSchedule() }
            )
        }
    }
}

@Composable
fun GreetingSection(userName: String) {
    Text(
        text = "Olá, $userName",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun NextAppointmentCard(appointment: Appointment, onSeeDetailsClicked: () -> Unit) {
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
            Spacer(modifier = Modifier.height(8.dp))
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
                onClick = onSeeDetailsClicked,
                modifier = Modifier
                    .align(Alignment.End)
                    .semantics { contentDescription = "Ver detalhes do agendamento" }
            ) {
                Text("Ver Detalhes")
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
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Ir para agendamento" }
            )
            Spacer(modifier = Modifier.size(8.dp))
            QuickActionButton(
                text = "Serviços",
                icon = Icons.Filled.ContentCut,
                onClick = onServicesClicked,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Ver serviços" }
            )
            Spacer(modifier = Modifier.size(8.dp))
            QuickActionButton(
                text = "Barbeiros",
                icon = Icons.Filled.Groups,
                onClick = onBarbersClicked,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Ver barbeiros" }
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
fun QuickRebookCard(service: Service, onRebookClicked: () -> Unit) {
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
            Spacer(modifier = Modifier.height(8.dp))
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
                modifier = Modifier
                    .align(Alignment.End)
                    .semantics { contentDescription = "Agendar novamente ${service.name}" }
            ) {
                Icon(Icons.Filled.Repeat, contentDescription = "Reagendar", modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Agendar Novamente")
            }
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen Preview")
@Composable
fun HomeScreenPreview() {
    val sampleState = HomeUiState.Content(
        userName = "Eduardo",
        nextAppointment = Appointment("1", "24/09", "15:00", "Corte + Barba", "Fernando Silva", "Confirmado"),
        lastServiceForRebooking = Service("s1", "Corte Masculino", 50.0, 45, Icons.Filled.ContentCut)
    )
    MaterialTheme {
        HomeContent(
            uiState = sampleState,
            onNavigateToSchedule = {},
            onNavigateToServices = {},
            onNavigateToBarbers = {},
            onNavigateToAppointmentDetails = {}
        )
    }
}

@Preview(showBackground = true, name = "Next Appointment Card Preview")
@Composable
fun NextAppointmentCardPreview() {
    MaterialTheme {
        NextAppointmentCard(
            appointment = Appointment("1", "24/09", "15:00", "Corte + Barba", "Fernando Silva", "Confirmado"),
            onSeeDetailsClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "Quick Rebook Card Preview")
@Composable
fun QuickRebookCardPreview() {
    MaterialTheme {
        QuickRebookCard(
            service = Service("s1", "Corte Masculino", 50.0, 45, Icons.Filled.ContentCut),
            onRebookClicked = {}
        )
    }
}
