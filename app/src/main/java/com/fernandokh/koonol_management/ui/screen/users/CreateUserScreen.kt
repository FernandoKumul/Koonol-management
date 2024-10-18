package com.fernandokh.koonol_management.ui.screen.users

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.DropdownMenuC
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.MenuItem.Divider
import com.fernandokh.koonol_management.utils.MenuItem.Option

@Composable
fun CreateUserScreen(navController: NavHostController) {
    var openMenu by remember { mutableStateOf(false) }

    val options = listOf(
        Option(
            "Tomar foto",
            ImageVector.vectorResource(R.drawable.ic_photo_camera),
            MaterialTheme.colorScheme.onBackground
        ),
        Divider,
        Option(
            "Seleccionar imagen",
            ImageVector.vectorResource(R.drawable.ic_gallery),
            MaterialTheme.colorScheme.onBackground
        ),
        Divider,
        Option(
            "Eliminar",
            ImageVector.vectorResource(R.drawable.ic_delete_bin_line),
            MaterialTheme.colorScheme.error
        ),
    )

    Scaffold(
        topBar = { TopBarGoBack("Crear Usuario", navController) },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Box {
                    Image(
                        painter = painterResource(R.drawable.default_user),
                        contentDescription = "img_user",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(96.dp)
                    )

                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = { openMenu = true },
                        modifier = Modifier.align(Alignment.BottomEnd).border(3.dp, MaterialTheme.colorScheme.background, CircleShape).size(40.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.ic_edit_2_line),
                            contentDescription = "ic_filter"
                        )
                        
                        DropdownMenuC(
                            expanded = openMenu,
                            onDismiss = { openMenu = false },
                            options = options,
                            onItemClick = {  }
                        )
                    }

                }
            }
        },
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PrevCreateUserScreen() {
    val navController = rememberNavController()
    KoonolmanagementTheme(dynamicColor = false) {
        CreateUserScreen(navController)
    }
}