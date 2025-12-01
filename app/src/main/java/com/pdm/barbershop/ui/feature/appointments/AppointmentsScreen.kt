package com.pdm.barbershop.ui.feature.appointments

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pdm.barbershop.domain.model.Appointment
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    viewModel: AppointmentsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccessMessage()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Agendamentos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
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
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Seção de Filtros
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = uiState.currentFilter == AppointmentFilter.ALL,
                                    onClick = { viewModel.setFilter(AppointmentFilter.ALL) },
                                    label = {
                                        Text("Todos (${uiState.appointments.size})")
                                    },
                                    leadingIcon = if (uiState.currentFilter == AppointmentFilter.ALL) {
                                        { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null
                                )
                            }
                            item {
                                FilterChip(
                                    selected = uiState.currentFilter == AppointmentFilter.SCHEDULED,
                                    onClick = { viewModel.setFilter(AppointmentFilter.SCHEDULED) },
                                    label = {
                                        Text("Agendados (${uiState.appointments.count { 
                                            it.status.uppercase() in listOf("SCHEDULED", "CONFIRMED", "AGENDADO")
                                        }})")
                                    },
                                    leadingIcon = if (uiState.currentFilter == AppointmentFilter.SCHEDULED) {
                                        { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null
                                )
                            }
                            item {
                                FilterChip(
                                    selected = uiState.currentFilter == AppointmentFilter.CANCELLED,
                                    onClick = { viewModel.setFilter(AppointmentFilter.CANCELLED) },
                                    label = {
                                        Text("Cancelados (${uiState.appointments.count { 
                                            it.status.uppercase() in listOf("CANCELLED", "CANCELADO")
                                        }})")
                                    },
                                    leadingIcon = if (uiState.currentFilter == AppointmentFilter.CANCELLED) {
                                        { Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null
                                )
                            }
                            item {
                                FilterChip(
                                    selected = uiState.currentFilter == AppointmentFilter.COMPLETED,
                                    onClick = { viewModel.setFilter(AppointmentFilter.COMPLETED) },
                                    label = {
                                        Text("Concluídos (${uiState.appointments.count { 
                                            it.status.uppercase() in listOf("COMPLETED", "CONCLUIDO")
                                        }})")
                                    },
                                    leadingIcon = if (uiState.currentFilter == AppointmentFilter.COMPLETED) {
                                        { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null
                                )
                            }
                        }

                        // Lista de Agendamentos Filtrados
                        if (uiState.filteredAppointments.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Nenhum agendamento encontrado nesta categoria.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 16.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                items(uiState.filteredAppointments) { appointment ->
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
        }
    }

    // Reschedule Dialog
    if (uiState.showRescheduleDialog && uiState.rescheduleAppointment != null) {
        RescheduleDialog(
            appointment = uiState.rescheduleAppointment!!,
            availableDates = uiState.availableDates,
            selectedDate = uiState.selectedDate,
            availableSlots = uiState.availableSlots,
            selectedSlot = uiState.selectedSlot,
            loadingSlots = uiState.loadingSlots,
            onDismiss = { viewModel.closeRescheduleDialog() },
            onDateSelected = { viewModel.selectDate(it) },
            onSlotSelected = { viewModel.selectSlot(it) },
            onConfirm = { viewModel.confirmReschedule() }
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
    val offsetDateTime = try {
        OffsetDateTime.parse(appointment.startTime)
    } catch (e: Exception) {
        null
    }
    
    val dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val date = offsetDateTime?.format(dateFormatter) ?: appointment.startTime
    val time = offsetDateTime?.format(timeFormatter) ?: ""

    val isCancelled = appointment.status.uppercase() in listOf("CANCELLED", "CANCELADO")

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header: Status e ID
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

            Spacer(modifier = Modifier.height(20.dp))

            // Nome do Serviço - Destaque Principal
            Text(
                text = appointment.serviceName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Informações do Agendamento
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Barbeiro",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Barbeiro",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = appointment.barberName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Data e Hora",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Data e Horário",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$date às $time",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Preço Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "R$ ${String.format("%.2f", appointment.totalPrice ?: 0.0)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Mostrar botões apenas se não estiver cancelado
            if (!isCancelled) {
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão Editar
                    TextButton(
                        onClick = onEditClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Editar",
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botão Cancelar
                    TextButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Cancelar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cancelar",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    // Definindo cores básicas manualmente
    val successContainer = Color(0xFFE8F5E9)
    val successGreen = Color(0xFF2E7D32)
    val infoContainer = Color(0xFFE3F2FD)
    val infoBlue = Color(0xFF1565C0)
    val errorContainer = Color(0xFFFFEBEE)
    val errorRed = Color(0xFFC62828)

    val (containerColor, contentColor, text) = when (status.uppercase()) {
        "SCHEDULED", "AGENDADO" -> Triple(successContainer, successGreen, "Agendado")
        "COMPLETED", "CONCLUIDO" -> Triple(infoContainer, infoBlue, "Concluído")
        "CANCELLED", "CANCELADO" -> Triple(errorContainer, errorRed, "Cancelado")
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            status
        )
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.dp
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
    availableDates: List<java.time.LocalDate>,
    selectedDate: java.time.LocalDate?,
    availableSlots: List<String>,
    selectedSlot: String?,
    loadingSlots: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (java.time.LocalDate) -> Unit,
    onSlotSelected: (String) -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reagendar Agendamento",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                // Subtítulo
                Text(
                    text = "Selecione uma nova data e horário:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Label "Data"
                Text(
                    text = "Data",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Seleção de Data
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    items(availableDates) { date ->
                        val isSelected = date == selectedDate
                        DateChipItem(
                            date = date,
                            isSelected = isSelected,
                            onClick = { onDateSelected(date) }
                        )
                    }
                }

                // Seleção de Horário
                if (selectedDate != null) {
                    // Label "Horário"
                    Text(
                        text = "Horário",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    when {
                        loadingSlots -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        availableSlots.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .background(
                                        MaterialTheme.colorScheme.errorContainer,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nenhum horário disponível para esta data.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.heightIn(max = 240.dp)
                            ) {
                                items(availableSlots) { slot ->
                                    val isSelected = slot == selectedSlot
                                    val formatter = java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                    val dateTime = java.time.OffsetDateTime.parse(slot, formatter)
                                    val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")

                                    TimeSlotItem(
                                        time = dateTime.format(timeFormatter),
                                        isSelected = isSelected,
                                        onClick = { onSlotSelected(slot) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onConfirm,
                    enabled = selectedSlot != null && !loadingSlots,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.widthIn(min = 100.dp)
                ) {
                    Text(
                        text = "Confirmar",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = null
    )
}

@Composable
fun DateChipItem(
    date: java.time.LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM")

    Surface(
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(12.dp),
        border = if (!isSelected)
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        else
            null,
        modifier = Modifier
            .height(40.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.format(dateFormatter),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun TimeSlotItem(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(12.dp),
        border = if (!isSelected)
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        else
            null,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
