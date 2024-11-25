package com.fernandokh.koonol_management.ui.components.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun InformationField(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null
) {
    Column(modifier) {

        if (title != "") {
            Text(
                title,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text, modifier = Modifier
                .padding(0.dp, 6.dp, 0.dp, 4.dp)
                .weight(1f))
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = "icon",
                    tint = MaterialTheme.colorScheme.outlineVariant

                )
            }
        }
        HorizontalDivider()
    }
}