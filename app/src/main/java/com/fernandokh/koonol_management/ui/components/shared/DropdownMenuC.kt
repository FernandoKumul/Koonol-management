package com.fernandokh.koonol_management.ui.components.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fernandokh.koonol_management.utils.MenuItem
import com.fernandokh.koonol_management.utils.MenuItem.Divider
import com.fernandokh.koonol_management.utils.MenuItem.Option

@Composable
fun DropdownMenuC(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onItemClick: (Option) -> Unit,
    options: List<MenuItem>
) {
    DropdownMenu(
        expanded = expanded,
//        modifier = Modifier.shadow(),
        onDismissRequest = { onDismiss() }
//                Modifier.background(MaterialTheme.colorScheme.background)
    ) {

        options.forEach { option ->
            when (option) {
                is Option -> {
                    DropdownMenuItem(
                        onClick = {
                            onDismiss()
                            onItemClick(option)
                        },
                        text = {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (option.icon != null) {
                                        Icon(
                                            tint = option.color,
                                            imageVector = option.icon,
                                            contentDescription = option.name
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = option.name, color = option.color)
                                }
                            }
                        }
                    )
                }
                Divider -> HorizontalDivider()
            }
        }
    }
}