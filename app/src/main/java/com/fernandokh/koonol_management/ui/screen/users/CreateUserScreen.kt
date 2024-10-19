package com.fernandokh.koonol_management.ui.screen.users

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.MyUploadImage
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import java.io.File

@Composable
fun CreateUserScreen(navController: NavHostController) {
    val context = LocalContext.current
    val cacheDir: File = context.cacheDir

    var imageUrl by remember { mutableStateOf<String?>(null) }


    Scaffold(
        topBar = { TopBarGoBack("Crear Usuario", navController) },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                MyUploadImage(
                    directory = File(cacheDir, "images"),
                    url = imageUrl,
                    onSetImage = { imageUrl = it })
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