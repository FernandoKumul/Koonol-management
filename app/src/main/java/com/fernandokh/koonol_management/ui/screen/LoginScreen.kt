package com.fernandokh.koonol_management.ui.screen

import android.content.ClipData.Item
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme

@Composable
fun LoginScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "¡Bienvenido a Konool para Gestores!",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(60.dp),
            fontSize = 24.sp
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White, shape = RoundedCornerShape(
                        topStart = 60.dp,
                        topEnd = 60.dp,
                    )
                )
        ) {
            Text(
                "Iniciar sesión",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            )

            Text("Correo electrónico")
            Text("Contraseña")
            Button(onClick = { navController.navigate(Screen.Menu.route) }) {
                Text("Continuar")
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LoginScreenPreview() {
    KoonolmanagementTheme(dynamicColor = false) {
        LoginScreen(
            navController = rememberNavController()
        )
    }
}