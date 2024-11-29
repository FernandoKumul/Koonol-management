package com.fernandokh.koonol_management.ui.screen.tianguis

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.CustomTextField
import com.fernandokh.koonol_management.ui.components.shared.MyUploadImage
import com.fernandokh.koonol_management.viewModel.tianguis.EditTianguisViewModel
import com.fernandokh.koonol_management.viewModel.tianguis.NavigationEvent
import java.io.File
import android.util.Log
import com.fernandokh.koonol_management.ui.components.maps.MapComponent
import com.fernandokh.koonol_management.ui.components.shared.UploadImage
import com.fernandokh.koonol_management.viewModel.AuthViewModel
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun EditTianguisScreen(
    navController: NavHostController,
    tianguisId: String?,
    authViewModel: AuthViewModel,
    editTianguisViewModel: EditTianguisViewModel // Cambiar de viewModel a editTianguisViewModel
) {
    val isTianguis by editTianguisViewModel.tianguis.collectAsState()
    val isLoading by editTianguisViewModel.isLoading.collectAsState()
    val userId by authViewModel.userId.collectAsState()
    Log.d("EditTianguisScreen", "UserId (AuthViewModel): $userId")

    LaunchedEffect(Unit) {
        if (userId == null) {
            // Intentamos obtener el token directamente desde DataStore
            val token = authViewModel.tokenManager.accessToken.firstOrNull()
            if (!token.isNullOrEmpty()) {
                val decodedJwt = authViewModel.decodeJwt(token)
                val decodedUserId = decodedJwt?.optString("userId")
                if (!decodedUserId.isNullOrEmpty()) {
                    authViewModel.setUserId(decodedUserId)
                    Log.d("EditTianguisScreen", "UserId recuperado de token: $decodedUserId")
                } else {
                    Log.e("EditTianguisScreen", "No se pudo decodificar el token")
                }
            } else {
                Log.e("EditTianguisScreen", "Token no encontrado, redirigiendo a login...")
                navController.navigate(Screen.Login.route)
            }
        } else {
            Log.d("EditTianguisScreen", "UserId ya disponible: $userId")
        }
    }

    val context = LocalContext.current
    val cacheDir: File = context.cacheDir

    val imageUrl by editTianguisViewModel.photo.collectAsState()
    val isLoadingUpdate by editTianguisViewModel.isLoadingUpdate.collectAsState()
    val isShowDialog by editTianguisViewModel.isShowDialog.collectAsState()
    val toastMessage by editTianguisViewModel.toastMessage.collectAsState()

    // Logs para verificar el estado inicial
    Log.d("EditTianguisScreen", "Tianguis ID: $tianguisId")
    Log.d("EditTianguisScreen", "UserId (AuthViewModel): $userId")

    LaunchedEffect(Unit) {
        // Verificar si userId es null en este punto
        Log.d("EditTianguisScreen", "LaunchedEffect -> userId: $userId")

        editTianguisViewModel.getTianguis(tianguisId)
        editTianguisViewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.TianguisCreated -> {
                    Log.d("EditTianguisScreen", "Navegando a la pantalla de Tianguis")
                    navController.navigate(Screen.Tianguis.route)
                }
            }
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            Log.d("EditTianguisScreen", "Toast Message: $it")
            editTianguisViewModel.resetToastMessage()
        }
    }

    Scaffold(
        topBar = { TopBarGoBack("Editar Tianguis", navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val isValid = editTianguisViewModel.isFormValid()
                    Log.d("EditTianguisScreen", "Formulario válido: $isValid")
                    if (isValid) {
                        Log.d("EditTianguisScreen", "Mostrando diálogo de confirmación")
                        editTianguisViewModel.showDialog()
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_save_line),
                    contentDescription = "Guardar"
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    isTianguis == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontró el tianguis",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(0.dp, 16.dp, 0.dp, 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            UploadImage(
                                directory = File(cacheDir, "images"),
                                url = imageUrl,
                                onSetImage = {
                                    Log.d("EditTianguisScreen", "Imagen seleccionada: $it")
                                    editTianguisViewModel.onPhotoChange(it)
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            FormTianguis(editTianguisViewModel)

                            Spacer(modifier = Modifier.height(20.dp))

                            // Agregar el mapa
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
                                initialLatitude = isTianguis?.markerMap?.coordinates?.get(1) ?: 19.4326,
                                initialLongitude = isTianguis?.markerMap?.coordinates?.get(0) ?: -99.1332,
                                enableFullScreen = true,
                                onLocationSelected = { newPosition ->
                                    Log.d(
                                        "EditTianguisScreen",
                                        "Nueva posición seleccionada: Lat: ${newPosition.latitude}, Lng: ${newPosition.longitude}"
                                    )
                                    editTianguisViewModel.updateCoordinates(
                                        newPosition.latitude,
                                        newPosition.longitude
                                    )
                                }
                            )

                            if (isShowDialog) {
                                AlertDialogC(
                                    dialogTitle = "Editar Tianguis",
                                    dialogText = "¿Estás seguro de los nuevos datos para el tianguis?",
                                    onDismissRequest = {
                                        Log.d("EditTianguisScreen", "Diálogo cancelado")
                                        editTianguisViewModel.dismissDialog()
                                    },
                                    onConfirmation = {
                                        Log.d("EditTianguisScreen", "Confirmación de edición iniciada")
                                        if (userId != null) {
                                            Log.d("EditTianguisScreen", "Llamando a updateTianguis con userId: $userId y tianguisId: $tianguisId")
                                            tianguisId?.let { editTianguisViewModel.updateTianguis(it, userId ?: "") }
                                        } else {
                                            Log.e("EditTianguisScreen", "Usuario no autenticado")
                                            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    loading = isLoadingUpdate
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}


@Composable
private fun FormTianguis(viewModel: EditTianguisViewModel) {
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

