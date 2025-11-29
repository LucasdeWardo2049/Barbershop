package com.pdm.barbershop.ui.feature.barber

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdm.barbershop.ui.feature.schedule.MonthlyCalendar
import java.time.LocalTime
import java.time.YearMonth

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
            // Como agora é uma aba inferior, talvez não precise de botão de voltar,
            // mas mantemos para consistência se for acessado de outro lugar
            // ou removemos se for raiz de aba.
            // No contexto de BottomNav, geralmente é o título da tela sem seta de voltar,
            // a menos que seja sub-tela. Como é aba principal agora, podemos simplificar.
            CenterAlignedTopAppBar(
                title = { Text("Gerenciar Horários", fontWeight = FontWeight.Bold) }
                // Sem navigationIcon se for aba principal
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

            // 2. Seleção de Horários
            Text("Definir Horário de Trabalho", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Início
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

                // Fim
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

            // Espaçamento para empurrar o botão para baixo se houver pouco conteúdo, 
            // ou para garantir espaço se houver scroll
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveWorkingHours() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salvar Horário")
                }
            }
        }
    }
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
