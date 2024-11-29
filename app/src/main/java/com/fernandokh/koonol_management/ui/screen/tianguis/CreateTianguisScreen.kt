package com.fernandokh.koonol_management.ui.screen.tianguis

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.components.maps.MapComponent
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.CustomTextField
import com.fernandokh.koonol_management.ui.components.shared.MyUploadImage
import com.fernandokh.koonol_management.viewModel.tianguis.CreateTianguisViewModel
import com.fernandokh.koonol_management.viewModel.tianguis.NavigationEvent
import java.io.File
import android.util.Log
import com.fernandokh.koonol_management.ui.components.shared.UploadImage
import com.fernandokh.koonol_management.viewModel.AuthViewModel
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun CreateTianguisScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    viewModel: CreateTianguisViewModel = viewModel()
) {
    val context = LocalContext.current
    val cacheDir: File = context.cacheDir

    // Variables de estado
    val imageUrl by viewModel.photo.collectAsState()
    val isLoadingCreate by viewModel.isLoadingCreate.collectAsState()
    val isShowDialog by viewModel.isShowDialog.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()
    val userId by authViewModel.userId.collectAsState()

    Log.d("CreateTianguisScreen", "UserId (AuthViewModel): $userId")

    // Recuperar el userId desde el token si es necesario
    LaunchedEffect(Unit) {
        if (userId == null) {
            val token = authViewModel.tokenManager.accessToken.firstOrNull()
            if (!token.isNullOrEmpty()) {
                val decodedJwt = authViewModel.decodeJwt(token)
                val decodedUserId = decodedJwt?.optString("userId")
                if (!decodedUserId.isNullOrEmpty()) {
                    authViewModel.setUserId(decodedUserId)
                    Log.d("CreateTianguisScreen", "UserId recuperado de token: $decodedUserId")
                } else {
                    Log.e("CreateTianguisScreen", "No se pudo decodificar el token")
                }
            } else {
                Log.e("CreateTianguisScreen", "Token no encontrado, redirigiendo a login...")
                navController.navigate(Screen.Login.route)
            }
        } else {
            Log.d("CreateTianguisScreen", "UserId ya disponible: $userId")
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Log.d("CreateTianguisScreen", "Mostrando Toast: $it")
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }

    // Lógica de navegación al crear el tianguis
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.TianguisCreated -> {
                    Log.d("CreateTianguisScreen", "Navegando a la pantalla de Tianguis")
                    navController.navigate(Screen.Tianguis.route)
                }
            }
        }
    }

    // Contenido de la pantalla
    Scaffold(
        topBar = { TopBarGoBack("Crear Tianguis", navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val isValid = viewModel.isFormValid()
                    Log.d("CreateTianguisScreen", "Validación del formulario: $isValid")
                    if (imageUrl == null) {
                        Toast.makeText(context, "Por favor, agrega una imagen.", Toast.LENGTH_SHORT).show()
                    } else if (isValid) {
                        viewModel.showDialog()
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(painter = painterResource(R.drawable.ic_save_line), contentDescription = "Guardar")
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(0.dp, 16.dp, 0.dp, 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UploadImage(
                    directory = File(cacheDir, "images"),
                    url = imageUrl,
                    onSetImage = {
                        viewModel.onPhotoChange(it)
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                FormTianguis(viewModel)
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Ubicación del Tianguis",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                MapComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    initialLatitude = latitude,
                    initialLongitude = longitude,
                    enableFullScreen = true,
                    onLocationSelected = { newPosition ->
                        viewModel.updateCoordinates(newPosition.latitude, newPosition.longitude)
                    }
                )

                if (isShowDialog) {
                    AlertDialogC(
                        dialogTitle = "Crear Tianguis",
                        dialogText = "¿Estás seguro de los datos para el nuevo tianguis?",
                        onDismissRequest = { viewModel.dismissDialog() },
                        onConfirmation = {
                            Log.d("CreateTianguisScreen", "Confirmación de creación con userId: $userId")
                            if (userId != null) {
                                viewModel.createTianguis(userId ?: "")
                            } else {
                                Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                            }
                        },
                        loading = isLoadingCreate
                    )
                }
            }
        }
    )
}


@Composable
private fun FormTianguis(viewModel: CreateTianguisViewModel) {
    val formErrors by viewModel.formErrors.collectAsState()
    val name by viewModel.name.collectAsState()
    val color by viewModel.color.collectAsState()
    val indications by viewModel.indications.collectAsState()
    val locality by viewModel.locality.collectAsState()

    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp, 24.dp)
            .fillMaxWidth(0.8f)
    ) {
        Text("Nombre", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            name,
            { viewModel.onNameChange(it) },
            "Nombre del Tianguis",
            error = formErrors.nameError != null,
            errorMessage = formErrors.nameError
        )
        Spacer(Modifier.height(16.dp))

        Text("Color", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            color,
            { viewModel.onColorChange(it) },
            "Color del Tianguis",
            error = formErrors.colorError != null,
            errorMessage = formErrors.colorError
        )
        Spacer(Modifier.height(16.dp))

        Text("Indicaciones", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            indications,
            { viewModel.onIndicationsChange(it) },
            "Indicaciones de la ubicación",
            error = formErrors.indicationsError != null,
            errorMessage = formErrors.indicationsError
        )
        Spacer(Modifier.height(16.dp))


        Text("Localidad", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            locality,
            { viewModel.onLocalityChange(it) },
            "Localidad del Tianguis",
            error = formErrors.localityError != null,
            errorMessage = formErrors.localityError
        )
    }
}
