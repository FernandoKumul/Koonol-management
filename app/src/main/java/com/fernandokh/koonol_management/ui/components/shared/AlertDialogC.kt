package com.fernandokh.koonol_management.ui.components.shared

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme

@Composable
fun AlertDialogC(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector = Icons.Outlined.Info,
    colorIcon: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    loading: Boolean = false
) {
    AlertDialog(
        icon = {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(36.dp))
            } else {
                Icon(
                    icon,
                    contentDescription = "Example Icon",
                    tint = colorIcon,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        title = {
            Text(text = dialogTitle, fontWeight = FontWeight.Medium)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            if (!loading) {
                onDismissRequest()
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                },
                enabled = !loading
            ) {
                Text("Confirmar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
                enabled = !loading
            ) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    )
}

@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PrevAlertDialogC() {
    KoonolmanagementTheme (dynamicColor = false) {
        AlertDialogC(
            dialogTitle = "¿Desea confirmar esta acción?",
            dialogText = "Al dar clic en confirmar, la acción no podrá ser revertida.",
            onConfirmation = {},
            onDismissRequest = {},
            loading = true
        )
    }
}