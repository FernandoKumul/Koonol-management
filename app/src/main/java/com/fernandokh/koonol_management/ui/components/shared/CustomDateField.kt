package com.fernandokh.koonol_management.ui.components.shared

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateField(
    onDateSelected: (String?) -> Unit,
    defaultDateISO: String? = null,
    placeholder: String = "Selecciona una fecha",
    error: Boolean = false,
    errorMessage: String? = null,
    noteMessage: String? = null
) {
    var validDate = true
    val defaultDateMillis: Long? = defaultDateISO?.let {
        try {
            val defaultDate = LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
            val millis = defaultDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            validDate = true
            millis
        } catch (e: Exception) {
            validDate = false
            null
        }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = defaultDateMillis)
    var isDatePickerVisible by remember { mutableStateOf(false) }

    val borderColor = MaterialTheme.colorScheme.outlineVariant
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 2f
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = borderColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp, 12.dp),
        onClick = { isDatePickerVisible = true }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (validDate) {
                Text(
                    text = defaultDateISO ?: placeholder,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (defaultDateISO == null) Color.Gray else MaterialTheme.colorScheme.onBackground
                )
            } else {
                Text(
                    text = "Fecha inválida",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "icon_arrow_down",
                tint = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
    if (error) {
        Text(
            text = errorMessage ?: "Error",
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp
        )
    } else if (noteMessage != null) {
        Text(
            text = noteMessage,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }

    if (isDatePickerVisible) {
        DatePickerDialog(
            onDismissRequest = { isDatePickerVisible = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    val selectedDateISO = selectedDateMillis?.let {
                        val selectedDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    onDateSelected(selectedDateISO)
                    isDatePickerVisible = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { isDatePickerVisible = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
fun PrevCustomDateField() {

    var date by remember { mutableStateOf<String?>("2016-06-01") }

    KoonolmanagementTheme(dynamicColor = false) {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomDateField(
                onDateSelected = { date = it },
                defaultDateISO = date,
            )
        }
    }
}