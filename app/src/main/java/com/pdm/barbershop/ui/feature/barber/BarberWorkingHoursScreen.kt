package com.pdm.barbershop.ui.feature.barber

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdm.barbershop.data.remote.dto.WorkingHourResponse
import com.pdm.barbershop.ui.feature.schedule.MonthlyCalendar
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberWorkingHoursScreen(
    viewModel: BarberWorkingHoursViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Estado para controle do diálogo de edição
    var showEditDialog by remember { mutableStateOf(false) }
    var editingWorkingHour by remember { mutableStateOf<WorkingHourResponse?>(null) }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gerenciar Horários", fontWeight = FontWeight.Bold) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Seleção de Data
            Text("Selecione a Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            val currentMonth = remember { mutableStateOf(YearMonth.now()) }
            Card(
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                MonthlyCalendar(
                    yearMonth = currentMonth.value,
                    selectedDate = uiState.selectedDate,
                    onDateSelected = { viewModel.selectDate(it) },
                    onMonthChanged = { currentMonth.value = it }
                )
            }

            // 2. Lista de Horários do Dia Selecionado
            if (uiState.selectedDate != null) {
                val dayName = uiState.selectedDate!!.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                
                Text("Horários para $dayName", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                if (uiState.workingHoursList.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Nenhum horário cadastrado para este dia.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    uiState.workingHoursList.forEach { workingHour ->
                        WorkingHourItemCard(
                            workingHour = workingHour,
                            onEdit = {
                                editingWorkingHour = it
                                showEditDialog = true
                            },
                            onDelete = { viewModel.deleteWorkingHour(it.workingHourId) }
                        )
                    }
                }

                // 3. Formulário de Adição
                Text("Adicionar Novo Horário", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            TimePickerButton(
                                label = "Início",
                                time = uiState.startTime,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    TimePickerDialog(
                                        context,
                                        { _, h, m -> viewModel.setStartTime(h, m) },
                                        uiState.startTime.hour,
                                        uiState.startTime.minute,
                                        true
                                    ).show()
                                }
                            )

                            TimePickerButton(
                                label = "Fim",
                                time = uiState.endTime,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    TimePickerDialog(
                                        context,
                                        { _, h, m -> viewModel.setEndTime(h, m) },
                                        uiState.endTime.hour,
                                        uiState.endTime.minute,
                                        true
                                    ).show()
                                }
                            )
                        }

                        Button(
                            onClick = { viewModel.saveWorkingHours() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Adicionar")
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Selecione uma data acima para ver e gerenciar os horários.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Dialog de Edição
    if (showEditDialog && editingWorkingHour != null) {
        EditWorkingHourDialog(
            workingHour = editingWorkingHour!!,
            onDismiss = { 
                showEditDialog = false
                editingWorkingHour = null
            },
            onConfirm = { start, end ->
                viewModel.updateWorkingHour(editingWorkingHour!!.workingHourId, start, end)
                showEditDialog = false
                editingWorkingHour = null
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkingHourItemCard(
    workingHour: WorkingHourResponse,
    onEdit: (WorkingHourResponse) -> Unit,
    onDelete: (WorkingHourResponse) -> Unit
) {
    // Parse times string HH:mm:ss -> HH:mm
    val startTime = try { LocalTime.parse(workingHour.startTime).toString().substring(0, 5) } catch (e: Exception) { workingHour.startTime }
    val endTime = try { LocalTime.parse(workingHour.endTime).toString().substring(0, 5) } catch (e: Exception) { workingHour.endTime }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$startTime - $endTime",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Row {
                IconButton(onClick = { onEdit(workingHour) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { onDelete(workingHour) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditWorkingHourDialog(
    workingHour: WorkingHourResponse,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime, LocalTime) -> Unit
) {
    val context = LocalContext.current
    var startTime by remember { 
        mutableStateOf(try { LocalTime.parse(workingHour.startTime) } catch (e: Exception) { LocalTime.of(9, 0) }) 
    }
    var endTime by remember { 
        mutableStateOf(try { LocalTime.parse(workingHour.endTime) } catch (e: Exception) { LocalTime.of(18, 0) }) 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Horário") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TimePickerButton(
                        label = "Início",
                        time = startTime,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            TimePickerDialog(context, { _, h, m -> startTime = LocalTime.of(h, m) }, startTime.hour, startTime.minute, true).show()
                        }
                    )
                    TimePickerButton(
                        label = "Fim",
                        time = endTime,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            TimePickerDialog(context, { _, h, m -> endTime = LocalTime.of(h, m) }, endTime.hour, endTime.minute, true).show()
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(startTime, endTime) }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerButton(
    label: String,
    time: LocalTime,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(
                String.format("%02d:%02d", time.hour, time.minute),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
