package com.fernandokh.koonol_management.ui.screen.scheduleTianguis

import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.CustomTextField
import com.fernandokh.koonol_management.ui.components.shared.CustomTimeField
import com.fernandokh.koonol_management.ui.components.shared.DropdownInputForm
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.viewModel.scheduleTianguis.CreateScheduleTianguisViewModel

@Composable
fun CreateScheduleTianguisScreen(
    navController: NavHostController,
    viewModel: CreateScheduleTianguisViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoadingCreate by viewModel.isLoadingCreate.collectAsState()
    val isShowDialog by viewModel.isShowDialog.collectAsState()

    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.Navigate -> {
                    navController.navigate(Screen.SalesStalls.route)
                }
            }
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }

    Scaffold(
        topBar = { TopBarGoBack("Crear Horario", navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val isValid = viewModel.isFormValid()
                    if (isValid) {
                        viewModel.showDialog()
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(painter = painterResource(R.drawable.ic_save_line), contentDescription = "Add")
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(0.dp, 16.dp, 0.dp, 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FormScheduleTianguis(viewModel)

                if (isShowDialog) {
                    AlertDialogC(
                        dialogTitle = "Crear Horario",
                        dialogText = "¿Estás seguro de los datos para el nuevo horario?",
                        onDismissRequest = { viewModel.dismissDialog() },
                        onConfirmation = { viewModel.createScheduleTianguis() },
                        loading = isLoadingCreate
                    )
                }

            }
        },
    )
}

@Composable
private fun FormScheduleTianguis(viewModel: CreateScheduleTianguisViewModel) {

    val formErrors by viewModel.formErrors.collectAsState()
    val tianguisId by viewModel.tianguisId.collectAsState()
    val dayWeek by viewModel.dayWeek.collectAsState()
    val dayWeekOptions = viewModel.daysOfWeek
    val indications by viewModel.indications.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()

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
        Text("Tianguis", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            tianguisId,
            { viewModel.onTianguisIdChange(it) },
            "Ingresa tu nombre",
            error = formErrors.tianguisIdError != null,
            errorMessage = formErrors.tianguisIdError
        )
        Spacer(Modifier.height(16.dp))
        Text("Día de la semana", color = MaterialTheme.colorScheme.onSurfaceVariant)
        DropdownInputForm(
            items = dayWeekOptions,
            selectedItem = dayWeekOptions.find { it.value == dayWeek },
            onItemSelected = { selectedDayWeek ->
                viewModel.onDayWeekChange(selectedDayWeek.value)
            },
            itemLabel = { it.name },
            label = "Selecciona una subcategoría",
        )
        Spacer(Modifier.height(16.dp))

        Text("Indicaciones", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            indications,
            { viewModel.onIndicationsChange(it) },
            "Ingresa las indicaciones",
            error = formErrors.indicationsError != null,
            errorMessage = formErrors.indicationsError
        )
        Spacer(Modifier.height(16.dp))

        Text("Hora de inicio", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTimeField(
            text = startTime,
            onTextChange = { viewModel.onStartTimeChange(it) },
            error = formErrors.startTimeError != null,
            errorMessage = formErrors.startTimeError
        )
        Spacer(Modifier.height(16.dp))

        Text("Hora de finalización", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTimeField(
            text = endTime,
            onTextChange = { viewModel.onEndTimeChange(it) },
            error = formErrors.endTimeError != null,
            errorMessage = formErrors.endTimeError
        )
    }
}