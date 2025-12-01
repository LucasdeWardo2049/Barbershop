package com.pdm.barbershop.ui.feature.barber

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdm.barbershop.domain.model.Appointment
import com.pdm.barbershop.ui.feature.appointments.RescheduleDialog
import com.pdm.barbershop.ui.feature.appointments.StatusChip
import com.pdm.barbershop.ui.feature.schedule.MonthlyCalendar
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BarberScheduleScreen(
    viewModel: BarberScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Se não houver TopBar global, descomente ou use um título simples
            // Text("Agenda", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Agenda do Barbeiro",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Calendário
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        MonthlyCalendar(
                            yearMonth = currentMonth.value,
                            selectedDate = uiState.selectedDate,
                            onDateSelected = { viewModel.onDateSelected(it) },
                            onMonthChanged = { currentMonth.value = it }
                        )
                    }
                }

                // Filtros
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == BarberAppointmentFilter.ALL,
                                onClick = { viewModel.setFilter(BarberAppointmentFilter.ALL) },
                                label = { Text("Todos") },
                                leadingIcon = if (uiState.currentFilter == BarberAppointmentFilter.ALL) {
                                    { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == BarberAppointmentFilter.SCHEDULED,
                                onClick = { viewModel.setFilter(BarberAppointmentFilter.SCHEDULED) },
                                label = { Text("Agendados") },
                                leadingIcon = if (uiState.currentFilter == BarberAppointmentFilter.SCHEDULED) {
                                    { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == BarberAppointmentFilter.CANCELLED,
                                onClick = { viewModel.setFilter(BarberAppointmentFilter.CANCELLED) },
                                label = { Text("Cancelados") },
                                leadingIcon = if (uiState.currentFilter == BarberAppointmentFilter.CANCELLED) {
                                    { Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == BarberAppointmentFilter.COMPLETED,
                                onClick = { viewModel.setFilter(BarberAppointmentFilter.COMPLETED) },
                                label = { Text("Concluídos") },
                                leadingIcon = if (uiState.currentFilter == BarberAppointmentFilter.COMPLETED) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Agendamentos para ${uiState.selectedDate.dayOfMonth}/${uiState.selectedDate.monthValue}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (uiState.filteredAppointments.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Nenhum agendamento encontrado.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.filteredAppointments) { appointment ->
                        BarberAppointmentCard(
                            appointment = appointment,
                            onEditClick = { viewModel.openRescheduleDialog(appointment) },
                            onCancelClick = { viewModel.cancelAppointment(appointment.appointmentId) }
                        )
                    }
                }
            }

            // Dialog de Reagendamento
            if (uiState.showRescheduleDialog && uiState.rescheduleAppointment != null) {
                val today = LocalDate.now()
                val availableDates = List(30) { today.plusDays(it.toLong()) }
                
                RescheduleDialog(
                    appointment = uiState.rescheduleAppointment!!,
                    availableDates = availableDates,
                    selectedDate = uiState.selectedDate,
                    availableSlots = uiState.availableSlots,
                    selectedSlot = uiState.selectedSlot,
                    loadingSlots = uiState.loadingSlots,
                    onDismiss = { viewModel.closeRescheduleDialog() },
                    onDateSelected = { viewModel.onDateSelected(it); viewModel.openRescheduleDialog(uiState.rescheduleAppointment!!) }, 
                    onSlotSelected = { viewModel.selectSlot(it) },
                    onConfirm = { viewModel.confirmReschedule() }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BarberAppointmentCard(
    appointment: Appointment,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val offsetDateTime = try {
        OffsetDateTime.parse(appointment.startTime)
    } catch (e: Exception) { null }

    val dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    val date = offsetDateTime?.format(dateFormatter) ?: appointment.startTime
    val time = offsetDateTime?.format(timeFormatter) ?: appointment.startTime.substring(11, 16)

    val isCancelled = appointment.status.uppercase() in listOf("CANCELLED", "CANCELADO")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header com Hora e Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                StatusChip(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${appointment.serviceName} com ${appointment.clientName}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cliente: ${appointment.clientName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Ações e Preço
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "R$ ${String.format("%.2f", appointment.totalPrice ?: 0.0)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (!isCancelled) {
                    Row {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Reagendar", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = onCancelClick) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Cancelar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
