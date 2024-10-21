package com.fernandokh.koonol_management.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.viewModel.AuthViewModel

@Composable
fun LoginScreen(navController: NavHostController, viewModel: AuthViewModel = viewModel()) {
    val accessToken by viewModel.accessToken.collectAsState()
    val email: String by viewModel.email.collectAsState()
    val password: String by viewModel.password.collectAsState()
    var passwordVisibility by remember { mutableStateOf(false) }

    val icon = if (passwordVisibility) {
        painterResource(id = R.drawable.baseline_visibility_24)
    } else {
        painterResource(id = R.drawable.baseline_visibility_off_24)
    }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(innerPadding)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("¡")
                        }
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                            append("Bienvenido a ")
                        }
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("Konool ")
                        }
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                            append("para ")
                        }
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("Gestores!")
                        }
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(60.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(
                                topStart = 60.dp,
                                topEnd = 60.dp,
                            )
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 46.dp)
                    ) {
                        Text(
                            "Iniciar sesión",
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp)
                        )
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(120.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        )
                    }
                    Column(modifier = Modifier.padding(32.dp)) {
                        Text("Correo electrónico", color = MaterialTheme.colorScheme.secondary)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.changeEmail(it) },
                            shape = RoundedCornerShape(15.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            maxLines = 1
                        )
                        Text("Contraseña", color = MaterialTheme.colorScheme.secondary)
                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.changePassword(it) },
                            shape = RoundedCornerShape(15.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            maxLines = 1,
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisibility = !passwordVisibility
                                }) {
                                    Icon(
                                        painter = icon,
                                        contentDescription = "Visibility",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisibility) VisualTransformation.None
                            else PasswordVisualTransformation()
                        )
                        Text(
                            accessToken,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.login()
                            if (accessToken.isNotEmpty()) {
                                navController.navigate(Screen.Menu.route)
                            }else{
                                navController.navigate(Screen.Users.route)
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 32.dp)
                    ) {
                        Text("Empezar", fontSize = 20.sp)
                    }
                }
            }
        },
    )

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