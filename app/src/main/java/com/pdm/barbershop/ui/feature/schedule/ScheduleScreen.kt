package com.pdm.barbershop.ui.feature.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm.barbershop.domain.model.Barber
import com.pdm.barbershop.domain.model.Service
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Passo 1: Seleção de Serviço
        SchedulingStep(title = "1. Escolha o serviço") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.services) { service ->
                    ServiceChip(
                        service = service,
                        isSelected = service == uiState.selectedService,
                        onServiceSelected = { viewModel.onServiceSelected(service) }
                    )
                }
            }
        }

        // Passo 2: Seleção de Barbeiro
        AnimatedVisibility(visible = uiState.selectedService != null) {
            SchedulingStep(title = "2. Escolha o profissional") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.barbers) { barber ->
                        BarberItem(
                            barber = barber,
                            isSelected = barber == uiState.selectedBarber,
                            onBarberSelected = { viewModel.onBarberSelected(barber) }
                        )
                    }
                }
            }
        }

        // Passo 3: Seleção de Data e Hora
        AnimatedVisibility(visible = uiState.selectedBarber != null) {
            SchedulingStep(title = "3. Escolha a data e o horário") {
                // Seleção de Data
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.availableDates) { date ->
                        DateChip(
                            date = date,
                            isSelected = date == uiState.selectedDate,
                            onDateSelected = { viewModel.onDateSelected(date) }
                        )
                    }
                }

                // Carregamento e Seleção de Horários
                if (uiState.isLoadingTimeSlots) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else if (uiState.selectedDate != null) {
                    if (uiState.availableTimeSlots.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 80.dp),
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .heightIn(min = 100.dp, max = 220.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.availableTimeSlots) { time ->
                                TimeSlotChip(
                                    time = time,
                                    isSelected = time == uiState.selectedTime,
                                    onTimeSelected = { viewModel.onTimeSelected(time) }
                                )
                            }
                        }
                    } else {
                        Text(
                            "Nenhum horário disponível para esta data.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card de Resumo
        AnimatedVisibility(visible = uiState.isConfirmationButtonEnabled) {
            AppointmentSummaryCard(uiState = uiState)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de confirmação
        Button(
            onClick = { /* TODO: Implementar a confirmação */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = uiState.isConfirmationButtonEnabled
        ) {
            Text("Confirmar Agendamento", fontSize = 16.sp)
        }
    }
}

@Composable
fun TimeSlotChip(time: String, isSelected: Boolean, onTimeSelected: () -> Unit) {
    AssistChip(
        onClick = onTimeSelected,
        label = { Text(time) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun DateChip(date: LocalDate, isSelected: Boolean, onDateSelected: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM")
    AssistChip(
        onClick = onDateSelected,
        label = { Text(date.format(formatter)) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun BarberItem(barber: Barber, isSelected: Boolean, onBarberSelected: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        onClick = onBarberSelected
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(barber.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ServiceChip(service: Service, isSelected: Boolean, onServiceSelected: () -> Unit) {
    AssistChip(
        onClick = onServiceSelected,
        label = { Text(service.name) },
        leadingIcon = { Icon(service.icon, contentDescription = null) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun SchedulingStep(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentSummaryCard(uiState: ScheduleUiState) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("pt", "BR"))

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Resumo do Agendamento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            val serviceSummary = uiState.selectedService?.let {
                "${it.name} (${currencyFormat.format(it.price)})"
            } ?: "-"

            val barberSummary = uiState.selectedBarber?.name ?: "-"
            val dateSummary = uiState.selectedDate?.format(dateFormatter) ?: "-"
            val timeSummary = uiState.selectedTime ?: "-"

            SummaryRow("Serviço:", serviceSummary)
            SummaryRow("Profissional:", barberSummary)
            SummaryRow("Data:", dateSummary)
            SummaryRow("Horário:", timeSummary)
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
        Text(value)
    }
}
