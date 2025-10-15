package com.pdm.barbershop.ui.feature.schedule

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm.barbershop.domain.model.Barber
import com.pdm.barbershop.domain.model.Service
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*

@SuppressLint("NewApi")
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = viewModel()
) {
    // Consumimos apenas o conteúdo consolidado para evitar problemas de pattern matching
    val uiState by viewModel.content.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Passo 1: Serviço
        SchedulingStep(title = "1. Escolha o serviço") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.services.size) { index ->
                    val service = uiState.services[index]
                    ServiceChip(
                        service = service,
                        isSelected = service == uiState.selectedService,
                        onServiceSelected = { viewModel.onServiceSelected(service) }
                    )
                }
            }
        }

        // Passo 2: Barbeiro
        AnimatedVisibility(
            visible = uiState.selectedService != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SchedulingStep(title = "2. Escolha o profissional") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.barbers.size) { index ->
                        val barber = uiState.barbers[index]
                        BarberItem(
                            barber = barber,
                            isSelected = barber == uiState.selectedBarber,
                            onBarberSelected = { viewModel.onBarberSelected(barber) }
                        )
                    }
                }
            }
        }

        // Passo 3: Data e Horário
        AnimatedVisibility(
            visible = uiState.selectedBarber != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SchedulingStep(title = "3. Escolha a data e o horário") {
                // Datas
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.availableDates.size) { index ->
                        val date = uiState.availableDates[index]
                        DateChip(
                            date = date,
                            isSelected = date == uiState.selectedDate,
                            onDateSelected = { viewModel.onDateSelected(date) }
                        )
                    }
                }

                // Horários
                when {
                    uiState.isLoadingTimeSlots -> {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    }
                    uiState.selectedDate != null && uiState.availableTimeSlots.isEmpty() -> {
                        Text(
                            "Nenhum horário disponível para esta data.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    uiState.availableTimeSlots.isNotEmpty() -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 80.dp),
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .heightIn(min = 100.dp, max = 220.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.availableTimeSlots.size) { index ->
                                val time = uiState.availableTimeSlots[index]
                                TimeSlotChip(
                                    time = time,
                                    isSelected = time == uiState.selectedTime,
                                    onTimeSelected = { viewModel.onTimeSelected(time) }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resumo
        AnimatedVisibility(
            visible = uiState.isConfirmationButtonEnabled,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AppointmentSummaryCard(uiState = uiState)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de confirmação
        Button(
            onClick = { /* TODO: Confirmar agendamento */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .semantics { contentDescription = "Confirmar agendamento" },
            enabled = uiState.isConfirmationButtonEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Confirmar Agendamento", fontSize = 16.sp)
        }
    }
}

@Composable
fun TimeSlotChip(time: String, isSelected: Boolean, onTimeSelected: () -> Unit) {
    BaseChip(
        label = time,
        isSelected = isSelected,
        onClick = onTimeSelected,
        modifier = Modifier.semantics { contentDescription = "Horário $time" }
    )
}

@Composable
fun DateChip(date: LocalDate, isSelected: Boolean, onDateSelected: () -> Unit) {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM")
    BaseChip(
        label = date.format(formatter),
        isSelected = isSelected,
        onClick = onDateSelected,
        modifier = Modifier.semantics { contentDescription = "Data ${date.format(formatter)}" }
    )
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
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.semantics { contentDescription = "Serviço ${service.name}" }
    )
}

@Composable
fun BaseChip(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )
}

@Composable
fun BarberItem(barber: Barber, isSelected: Boolean, onBarberSelected: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp)
            .clickable { onBarberSelected() }
            .semantics { contentDescription = "Profissional ${barber.name}" },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                barber.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
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

@Composable
fun AppointmentSummaryCard(uiState: ScheduleUiState) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale.forLanguageTag("pt-BR"))

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
