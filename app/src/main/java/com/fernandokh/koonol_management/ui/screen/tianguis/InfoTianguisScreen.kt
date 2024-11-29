package com.fernandokh.koonol_management.ui.screen.tianguis

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.data.models.TianguisModel
import com.fernandokh.koonol_management.ui.components.maps.MapComponent
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.InformationField
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.viewModel.tianguis.InfoTianguisViewModel
import android.util.Log
import com.fernandokh.koonol_management.data.models.ScheduleTianguisModel

@Composable
fun InfoTianguisScreen(
    navController: NavHostController,
    tianguisId: String?,
    viewModel: InfoTianguisViewModel = viewModel()
) {
    val isTianguis by viewModel.isTianguis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getTianguis(tianguisId)
    }

    Scaffold(
        topBar = { TopBarGoBack("Tianguis", navController) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
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
                        InfoTianguis(isTianguis as TianguisModel)
                    }
                }
            }
        },
    )
}

@Composable
private fun InfoTianguis(tianguis: TianguisModel) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(0.dp, 16.dp, 0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (tianguis.photo != null) {
            AsyncImage(
                model = tianguis.photo,
                contentDescription = "img_tianguis",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(200.dp)
                    .width(300.dp)
                    .clip(RoundedCornerShape(10.dp)),
                placeholder = painterResource(R.drawable.default_image)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.default_image),
                contentDescription = "img_tianguis",
                modifier = Modifier
                    .height(200.dp)
                    .width(300.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
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
            InformationField("Nombre", tianguis.name)
            Spacer(Modifier.height(24.dp))
            InformationField("Color", tianguis.color.ifBlank { "No especificado" })
            Spacer(Modifier.height(24.dp))
            InformationField("Indicaciones", tianguis.indications.ifBlank { "No especificadas" })
            Spacer(Modifier.height(24.dp))
            InformationField("Localidad", tianguis.locality)
            Spacer(Modifier.height(24.dp))
            if (tianguis.schedule?.isNotEmpty() == true) {
                Text("Horarios", color = MaterialTheme.colorScheme.onSurfaceVariant)
                tianguis.schedule.forEach { schedule ->
                    Row {
                        Text(schedule.dayWeek, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(10.dp))
                        Text("De: ${schedule.startTime} a ${schedule.endTime}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                Text("No hay horarios definidos", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        tianguis.markerMap?.coordinates?.let { coordinates ->
            if (coordinates.size == 2) {
                val longitude = coordinates[0]
                val latitude = coordinates[1]

                Log.d("MapDebug", "Latitude: $latitude, Longitude: $longitude")

                MapComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    initialLatitude = latitude,
                    initialLongitude = longitude,
                    enableFullScreen = false,
                    onLocationSelected = {} // El marcador no es interactivo
                )
            } else {
                Log.e("MapDebug", "Invalid coordinates size: ${coordinates.size}")
                Text(
                    text = "Ubicación no disponible",
                    color = MaterialTheme.colorScheme.error
                )
            }
        } ?:  Log.e("MapDebug", "No coordinates found")
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PrevInfoTianguisScreen() {
    val navController = rememberNavController()
    KoonolmanagementTheme(dynamicColor = false) {
        InfoTianguisScreen(navController, "Id")
    }
}
