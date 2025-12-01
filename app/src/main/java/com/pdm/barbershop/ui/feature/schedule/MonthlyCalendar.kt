package com.pdm.barbershop.ui.feature.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyCalendar(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(yearMonth.minusMonths(1)) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Mês Anterior")
            }
            
            Text(
                text = yearMonth.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).replaceFirstChar { it.uppercase() } + " " + yearMonth.year,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = { onMonthChanged(yearMonth.plusMonths(1)) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Próximo Mês")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        val daysInMonth = yearMonth.lengthOfMonth()
        val startOffset = if (yearMonth.atDay(1).dayOfWeek.value == 7) 0 else yearMonth.atDay(1).dayOfWeek.value
        val totalCells = daysInMonth + startOffset
        val rows = (totalCells + 6) / 7

        Column {
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    for (col in 0 until 7) {
                        val day = (row * 7 + col) - startOffset + 1
                        if (day in 1..daysInMonth) {
                            val date = yearMonth.atDay(day)
                            val isSelected = date == selectedDate
                            val isToday = date == LocalDate.now()
                            val isPast = date.isBefore(LocalDate.now())

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else if (isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        else Color.Transparent
                                    )
                                    .then(if (!isPast) Modifier.clickable(onClick = { onDateSelected(date) }) else Modifier), 
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else if (isPast) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
