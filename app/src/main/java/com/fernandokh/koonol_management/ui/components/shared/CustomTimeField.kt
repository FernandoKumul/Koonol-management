package com.fernandokh.koonol_management.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTimeField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String = "HH:mm",
    error: Boolean = false,
    errorMessage: String? = null
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    BasicTextField(
        value = text,
        onValueChange = { value ->
            // Validar que solo acepte formato de hora (HH:mm)
            if (value.length <= 5 && value.matches(Regex("^\\d{0,2}:?\\d{0,2}\$"))) {
                // Agregar automáticamente el separador ":" al escribir
                if (value.length == 2 && !value.contains(":")) {
                    onTextChange("$value:")
                } else {
                    onTextChange(value)
                }
            }
        },
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
            }
            .padding(horizontal = 0.dp, vertical = 12.dp),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        )
                    }
                    innerTextField()
                }
            }
        }
    )

    if (error) {
        Text(
            text = errorMessage ?: "Error",
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp
        )
    }
}
