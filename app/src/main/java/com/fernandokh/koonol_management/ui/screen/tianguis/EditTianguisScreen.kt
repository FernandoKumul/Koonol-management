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

@Composable
fun EditTianguisScreen(
    navController: NavHostController,
    tianguisId: String?,
    viewModel: EditTianguisViewModel = viewModel()
) {
    val isTianguis by viewModel.tianguis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val cacheDir: File = context.cacheDir

    val imageUrl by viewModel.photo.collectAsState()
    val isLoadingUpdate by viewModel.isLoadingUpdate.collectAsState()
    val isShowDialog by viewModel.isShowDialog.collectAsState()

    val toastMessage by viewModel.toastMessage.collectAsState()

    // Log initial parameters
    Log.d("EditTianguisScreen", "Initializing with tianguisId: $tianguisId")

    LaunchedEffect(Unit) {
        Log.d("EditTianguisScreen", "LaunchedEffect triggered")
        viewModel.getTianguis(tianguisId)
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.TianguisCreated -> {
                    Log.i("NavigationEvent", "Navigating to Tianguis screen")
                    navController.navigate(Screen.Tianguis.route)
                }
            }
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Log.d("ToastMessage", "Displaying toast: $it")
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }

    Scaffold(
        topBar = {
            Log.d("EditTianguisScreen", "TopBar rendered")
            TopBarGoBack("Editar Tianguis", navController)
        },
        floatingActionButton = {
            Log.d("EditTianguisScreen", "FloatingActionButton rendered")
            FloatingActionButton(
                onClick = {
                    val isValid = viewModel.isFormValid()
                    Log.d("FormValidation", "Form validation result: $isValid")
                    if (isValid) {
                        Log.d("Dialog", "Showing confirmation dialog")
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
            Log.d("EditTianguisScreen", "Content rendered")
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when {
                    isLoading -> {
                        Log.d("EditTianguisScreen", "Loading state")
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    isTianguis == null -> {
                        Log.w("EditTianguisScreen", "No Tianguis found")
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
                        Log.d("EditTianguisScreen", "Rendering Tianguis form")
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(0.dp, 16.dp, 0.dp, 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MyUploadImage(
                                directory = File(cacheDir, "images"),
                                url = imageUrl,
                                onSetImage = {
                                    Log.d("MyUploadImage", "Image updated: $it")
                                    viewModel.onPhotoChange(it)
                                })
                            Spacer(modifier = Modifier.height(20.dp))
                            FormTianguis(viewModel)

                            if (isShowDialog) {
                                Log.d("Dialog", "Confirmation dialog visible")
                                AlertDialogC(
                                    dialogTitle = "Editar Tianguis",
                                    dialogText = "¿Estás seguro de los nuevos datos para el tianguis?",
                                    onDismissRequest = {
                                        Log.d("Dialog", "Dialog dismissed")
                                        viewModel.dismissDialog()
                                    },
                                    onConfirmation = {
                                        Log.d("Dialog", "Confirmation dialog accepted")
                                        if (tianguisId != null) {
                                            Log.d("UpdateTianguis", "Updating Tianguis with ID: $tianguisId")
                                            viewModel.updateTianguis(tianguisId)
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
    val dayWeek by viewModel.dayWeek.collectAsState()
    val indications by viewModel.indications.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
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

        Text("Día de la Semana", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            dayWeek,
            { viewModel.onDayWeekChange(it) },
            "Día (ej. Lunes)",
            error = formErrors.dayWeekError != null,
            errorMessage = formErrors.dayWeekError
        )
        Spacer(Modifier.height(16.dp))

        Text("Indicaciones", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            indications,
            { viewModel.onIndicationsChange(it) },
            "Indicaciones del Tianguis",
            error = formErrors.indicationsError != null,
            errorMessage = formErrors.indicationsError
        )
        Spacer(Modifier.height(16.dp))

        Text("Hora de Inicio", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            startTime,
            { viewModel.onStartTimeChange(it) },
            "HH:MM",
            error = formErrors.startTimeError != null,
            errorMessage = formErrors.startTimeError
        )
        Spacer(Modifier.height(16.dp))

        Text("Hora de Fin", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            endTime,
            { viewModel.onEndTimeChange(it) },
            "HH:MM",
            error = formErrors.endTimeError != null,
            errorMessage = formErrors.endTimeError
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

@Preview(showBackground = true)
@Composable
fun PrevEditTianguisScreen() {
    val navController = rememberNavController()
    EditTianguisScreen(navController, "tianguisId")
}
