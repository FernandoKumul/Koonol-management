package com.fernandokh.koonol_management.ui.screen.locationSalesStalls

import android.util.Log
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
import com.fernandokh.koonol_management.ui.components.maps.MapComponent
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.DropdownInputForm
import com.fernandokh.koonol_management.viewModel.locationSalesStalls.CreateLocationSalesStallsViewModel
import com.fernandokh.koonol_management.viewModel.tianguis.NavigationEvent

@Composable
fun CreateLocationSalesStallsScreen(
    navController: NavHostController,
    salesStallId: String?,
    viewModel: CreateLocationSalesStallsViewModel = viewModel()
) {
    val context = LocalContext.current

    val isLoadingCreate by viewModel.isLoadingCreate.collectAsState()
    val isShowDialog by viewModel.isShowDialog.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Log.d("CreateTianguisScreen", "Mostrando Toast: $it")
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }

    LaunchedEffect(Unit) {
        if (salesStallId != null) {
            viewModel.onSalesStallsIdChange(salesStallId)
        } else {
            Log.e("CreateLocationSalesStallsScreen", "salesStallId es nulo")
        }
        viewModel.getAllTianguis()
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.TianguisCreated -> {
                    Log.d("CreateTianguisScreen", "Navegando a la pantalla de Tianguis")
                    navController.navigate(Screen.SalesStalls.route)
                }
            }
        }
    }

    Scaffold(
        topBar = { TopBarGoBack("Crear Ubicación", navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showDialog() },
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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(0.dp, 16.dp, 0.dp, 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FormLocationSalesStalls(viewModel)
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
                        dialogTitle = "Crear ubicación",
                        dialogText = "¿Estás seguro de los datos para la nueva ubicación?",
                        onDismissRequest = { viewModel.dismissDialog() },
                        onConfirmation = { viewModel.createLocationSalesStalls() },
                        loading = isLoadingCreate
                    )
                }
            }
        }
    )
}

@Composable
private fun FormLocationSalesStalls(
    viewModel: CreateLocationSalesStallsViewModel,
) {

    val tianguisList by viewModel.tianguisList.collectAsState()
    val tianguisId by viewModel.tianguisId.collectAsState()
    val scheduleTianguisList by viewModel.scheduleTianguisList.collectAsState()
    val scheduleTianguisId by viewModel.scheduleTianguisId.collectAsState()

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
        DropdownInputForm(
            items = tianguisList,
            selectedItem = tianguisList.find { it.id == tianguisId },
            onItemSelected = { selectedTianguis ->
                viewModel.onTianguisIdChange(selectedTianguis.id)
            },
            itemLabel = { it.name },
            label = "Selecciona un Tianguis",
        )
        Spacer(Modifier.height(16.dp))
        DropdownInputForm(
            items = scheduleTianguisList,
            selectedItem = scheduleTianguisList.find { it.id == scheduleTianguisId },
            onItemSelected = { selectedScheduleTianguis ->
                viewModel.onScheduleTianguisIdChange(selectedScheduleTianguis.id)
            },
            itemLabel = { "${it.dayWeek}, De: ${it.startTime} a ${it.endTime}" },
            label = "Selecciona un Horario",
        )
        Spacer(Modifier.height(16.dp))
    }
}