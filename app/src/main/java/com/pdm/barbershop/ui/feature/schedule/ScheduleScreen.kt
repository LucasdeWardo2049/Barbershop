package com.pdm.barbershop.ui.feature.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.pdm.barbershop.domain.model.Barber
import com.pdm.barbershop.domain.model.Service
import com.pdm.barbershop.util.DateTimeUtils
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
    onNavigateToPayment: () -> Unit = {}
) {
    val uiState by viewModel.ui.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            scope.launch {
                viewModel.consumeBookingSuccess()
                onNavigateToPayment()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Agenda",
                        style = MaterialTheme.typography.titleMedium,
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
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                ) {
                    item {
                        uiState.errorMessage?.let { err ->
                            ErrorBanner(message = err)
                        }
                    }

                    item {
                        SectionHeader(title = "Serviços", subtitle = "Selecione o procedimento")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.services) { service ->
                                ServiceCardSelector(
                                    service = service,
                                    isSelected = service == uiState.selectedService,
                                    onClick = { viewModel.selectService(service) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (uiState.selectedService != null) {
                        item {
                            SectionHeader(title = "Profissional", subtitle = "Quem vai te atender?")
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(uiState.barbers) { barber ->
                                    BarberCardSelector(
                                        barber = barber,
                                        isSelected = barber == uiState.selectedBarber,
                                        onClick = { viewModel.selectBarber(barber) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    if (uiState.selectedBarber != null) {
                        item {
                            SectionHeader(title = "Data", subtitle = "Escolha o melhor dia")
                            
                            val currentMonth = remember { mutableStateOf(YearMonth.now()) }
                            
                            // Card para o calendário para dar destaque
                            Card(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                MonthlyCalendar(
                                    yearMonth = currentMonth.value,
                                    selectedDate = uiState.selectedDate,
                                    onDateSelected = { viewModel.selectDate(it) },
                                    onMonthChanged = { currentMonth.value = it }
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        item {
                            SectionHeader(title = "Horário", subtitle = "Disponibilidade para o dia selecionado")
                            
                            if (uiState.loadingSlots) {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                                }
                            } else if (uiState.selectedDate != null) {
                                if (uiState.availability.isEmpty()) {
                                    EmptyStateMessage(message = "Nenhum horário disponível para esta data.")
                                } else {
                                    val rows = (uiState.availability.size + 3) / 4
                                    val gridHeight = (rows * 56).dp 
                                    
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(4),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .heightIn(min = 0.dp, max = 1000.dp)
                                            .height(gridHeight), 
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        userScrollEnabled = false
                                    ) {
                                        items(uiState.availability) { timeIso ->
                                            val label = DateTimeUtils.labelFromIso(timeIso, ZoneId.of("America/Sao_Paulo"))
                                            TimeSlotSelector(
                                                time = label,
                                                isSelected = timeIso == uiState.selectedTime,
                                                onClick = { viewModel.selectTime(timeIso) }
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    "Selecione uma data acima para ver os horários.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }

                    if (uiState.canBook) {
                        item {
                             AppointmentSummarySection(uiState = uiState)
                        }
                    }
                }

                if (uiState.canBook) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = { viewModel.book() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Confirmar Agendamento", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.loading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Processando...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorBanner(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ServiceCardSelector(service: Service, isSelected: Boolean, onClick: () -> Unit) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    
    val borderStroke = if (isSelected) 
        null 
    else 
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(defaultElevation = if(isSelected) 8.dp else 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    service.icon, 
                    contentDescription = null, 
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(service.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(service.price),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun BarberCardSelector(barber: Barber, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            border = BorderStroke(if(isSelected) 3.dp else 0.dp, borderColor),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (barber.imageUrl != null) {
                    AsyncImage(
                        model = barber.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp)
                    )
                }
                if (isSelected) {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)))
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            barber.name.split(" ").first(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TimeSlotSelector(time: String, isSelected: Boolean, onClick: () -> Unit) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    Surface(
        onClick = onClick,
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(8.dp),
        border = border,
        shadowElevation = if(isSelected) 4.dp else 1.dp,
        modifier = Modifier.height(40.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentSummarySection(uiState: ScheduleUiState) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("pt", "BR"))

    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Resumo do Agendamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Divider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
            
            SummaryRowItem("Serviço", uiState.selectedService?.name ?: "-")
            SummaryRowItem("Profissional", uiState.selectedBarber?.name ?: "-")
            SummaryRowItem("Data", uiState.selectedDate?.format(dateFormatter) ?: "-")
            SummaryRowItem("Horário", uiState.selectedTime?.let { DateTimeUtils.labelFromIso(it, ZoneId.of("America/Sao_Paulo")) } ?: "-")
            
            Divider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    uiState.selectedService?.let { currencyFormat.format(it.price) } ?: "R$ 0,00",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SummaryRowItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
