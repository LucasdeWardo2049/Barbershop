package com.pdm.barbershop.ui.feature.appointments

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pdm.barbershop.domain.model.Appointment
import com.pdm.barbershop.util.DateTimeUtils
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    viewModel: AppointmentsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show success message
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    // Botão de voltar removido conforme solicitado
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.errorMessage ?: "Erro ao carregar agendamentos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchAppointments() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
                uiState.appointments.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                         Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Você ainda não possui agendamentos.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.appointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onEditClick = { viewModel.openRescheduleDialog(appointment) },
                                onCancelClick = { viewModel.cancelAppointment(appointment.appointmentId) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Reschedule Dialog
    if (uiState.showRescheduleDialog) {
        RescheduleDialog(
            appointment = uiState.rescheduleAppointment!!,
            availableDates = uiState.availableDates,
            selectedDate = uiState.selectedDate,
            availableSlots = uiState.availableSlots,
            selectedSlot = uiState.selectedSlot,
            loadingSlots = uiState.loadingSlots,
            onDateSelected = { viewModel.selectRescheduleDate(it) },
            onSlotSelected = { viewModel.selectRescheduleSlot(it) },
            onConfirm = { viewModel.confirmReschedule() },
            onDismiss = { viewModel.closeRescheduleDialog() }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCard(
    appointment: Appointment,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    val offsetDateTime = OffsetDateTime.parse(appointment.startTime)
    val dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val date = offsetDateTime.format(dateFormatter)
    val time = offsetDateTime.format(timeFormatter)

    // Only show action buttons for SCHEDULED or CONFIRMED appointments
    val canModify = appointment.status.uppercase() in listOf("SCHEDULED", "CONFIRMED", "AGENDADO")

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(status = appointment.status)
                Text(
                    text = "#${appointment.appointmentId}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = appointment.serviceName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person, 
                    contentDescription = null, 
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = appointment.barberName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Schedule, 
                    contentDescription = null, 
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$date às $time",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (canModify) {
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Botão Editar sem fundo
                    TextButton(
                        onClick = onEditClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botão Cancelar sem fundo
                    TextButton(
                        onClick = { showCancelDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Cancelar", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancelar")
                    }
                }
            }
        }
    }

    // Cancel confirmation dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar Agendamento") },
            text = {
                Text("Tem certeza que deseja cancelar este agendamento? Esta ação não pode ser desfeita.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        onCancelClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sim, Cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Não")
                }
            }
        )
    }
}

@Composable
fun StatusChip(status: String) {
    val (containerColor, contentColor, text) = when (status.uppercase()) {
        "SCHEDULED", "AGENDADO" -> Triple(
            Color(0xFFE6F4EA), 
            Color(0xFF1E8E3E), 
            "Agendado"
        )
        "COMPLETED", "CONCLUIDO" -> Triple(
            Color(0xFFE8F0FE), 
            Color(0xFF1967D2), 
            "Concluído"
        )
        "CANCELLED", "CANCELADO" -> Triple(
            Color(0xFFFCE8E6), 
            Color(0xFFC5221F), 
            "Cancelado"
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            status
        )
    }

    Surface(
        color = containerColor,
        shape = CircleShape
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RescheduleDialog(
    appointment: Appointment,
    availableDates: List<LocalDate>,
    selectedDate: LocalDate?,
    availableSlots: List<String>,
    selectedSlot: String?,
    loadingSlots: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    onSlotSelected: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Reagendar",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = appointment.serviceName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                HorizontalDivider()

                // Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Selecione uma nova data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Date selector - horizontal scroll
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableDates) { date ->
                                DateChip(
                                    date = date,
                                    isSelected = date == selectedDate,
                                    onClick = { onDateSelected(date) }
                                )
                            }
                        }
                    }

                    item {
                        if (selectedDate != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Horários disponíveis",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            when {
                                loadingSlots -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                availableSlots.isEmpty() -> {
                                    Text(
                                        text = "Nenhum horário disponível para esta data.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 24.dp)
                                    )
                                }
                                else -> {
                                    // Time slots grid
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(4),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.heightIn(max = 400.dp),
                                        userScrollEnabled = false
                                    ) {
                                        items(availableSlots) { slot ->
                                            val label = DateTimeUtils.labelFromIso(
                                                slot,
                                                ZoneId.of("America/Sao_Paulo")
                                            )
                                            TimeSlotChip(
                                                time = label,
                                                isSelected = slot == selectedSlot,
                                                onClick = { onSlotSelected(slot) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer - Action buttons
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        enabled = selectedSlot != null
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateChip(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val formatterDay = DateTimeFormatter.ofPattern("dd")
    val formatterWeek = DateTimeFormatter.ofPattern("EEE", Locale("pt", "BR"))

    val containerColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurface
    val border = if (isSelected)
        null
    else
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    OutlinedCard(
        onClick = onClick,
        colors = CardDefaults.outlinedCardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = border ?: CardDefaults.outlinedCardBorder(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(width = 64.dp, height = 80.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                date.format(formatterWeek).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                date.format(formatterDay),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TimeSlotChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurface
    val border = if (isSelected)
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    else
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    Surface(
        onClick = onClick,
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(8.dp),
        border = border,
        modifier = Modifier.height(40.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

