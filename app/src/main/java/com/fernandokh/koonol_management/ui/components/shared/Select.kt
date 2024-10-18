package com.fernandokh.koonol_management.ui.components.shared

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme

@Composable
fun CustomSelect(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    fill: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedOption) }

    Box {
        if (fill) {
            Button(
                modifier = Modifier.fillMaxWidth(),
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
                    Text(selected, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { expanded = true }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selected, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "icon_arrow_down",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider()
            }
        }

        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    selected = option
                    onOptionSelected(option)
                    expanded = false
                }, text = {
                    Text(text = option)
                })
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun SampleScreen() {
    val options = listOf("Option 1", "Option 2", "Option 3")
    var selectedOption by remember { mutableStateOf(options[0]) }

    KoonolmanagementTheme(dynamicColor = false) {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomSelect(
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                fill = true
            )
        }
    }
}
