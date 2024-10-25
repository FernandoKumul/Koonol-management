package com.fernandokh.koonol_management.ui.components.shared

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.SelectOption

@Composable
fun CustomSelect(
    options: List<SelectOption>,
    selectedOption: SelectOption,
    onOptionSelected: (SelectOption) -> Unit,
    fill: Boolean = true,
    error: Boolean = false,
    errorMessage: String? = null,
    noteMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedOption) }
    var buttonWidth by remember { mutableIntStateOf(0) }


    Box {
        if (fill) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        buttonWidth = coordinates.size.width
                    },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),

                onClick = { expanded = true }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selected.text, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "icon_arrow_down",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column {
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            buttonWidth = coordinates.size.width
                        },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp, 12.dp),
                    onClick = { expanded = true }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            selected.text,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (selected.value == "") Color.Gray else MaterialTheme.colorScheme.onBackground
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "icon_arrow_down",
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }

        DropdownMenu(
            modifier = Modifier
                .width(with(LocalDensity.current) { buttonWidth.toDp() })
                .background(MaterialTheme.colorScheme.primaryContainer),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.filter { it.value.isNotEmpty() }.forEach { option ->
                DropdownMenuItem(onClick = {
                    selected = option
                    onOptionSelected(option)
                    expanded = false
                }, text = {
                    Text(text = option.text)
                })
            }
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
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
fun PrevCustomSelect() {
    val options = listOf(
        SelectOption("Opcion 1", "1"),
        SelectOption("Opcion 2", "2"),
        SelectOption("Opcion 3", "3"),
    )
    var selectedOption by remember { mutableStateOf(options[0]) }

    KoonolmanagementTheme(dynamicColor = false) {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomSelect(
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                fill = false
            )

            Text(selectedOption.value)
        }
    }
}
