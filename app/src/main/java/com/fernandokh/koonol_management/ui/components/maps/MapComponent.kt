package com.fernandokh.koonol_management.ui.components.maps

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.fernandokh.koonol_management.R

@Composable
fun MapComponent(
    modifier: Modifier = Modifier,
    initialLatitude: Double,
    initialLongitude: Double,
    zoom: Float = 15f, // Nivel de zoom inicial
    enableFullScreen: Boolean = true, // Bandera para habilitar pantalla completa
    onLocationSelected: (LatLng) -> Unit // Callback para enviar la nueva posición
) {
    // Estado para manejar el modo de pantalla completa
    var isFullScreen by remember { mutableStateOf(false) }

    // Estado para la posición de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(initialLatitude, initialLongitude), zoom)
    }

    // Detectar cambios en la posición de la cámara y enviar la nueva posición
    LaunchedEffect(cameraPositionState.position.target) {
        onLocationSelected(cameraPositionState.position.target)
        Log.d(
            "MapComponent",
            "Cámara movida a Lat: ${cameraPositionState.position.target.latitude}, Lng: ${cameraPositionState.position.target.longitude}"
        )
    }

    if (isFullScreen) {
        // Mapa en pantalla completa
        Dialog(onDismissRequest = { isFullScreen = false }) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GoogleMap(
                    modifier = Modifier
                        .height(500.dp)
                        .width(400.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    cameraPositionState = cameraPositionState
                )

                // Marcador centrado
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pin_google), // Icono personalizado
                        contentDescription = "Marcador centrado",
                        modifier = Modifier.size(48.dp) // Ajustar tamaño del marcador
                    )
                }
            }
        }
    } else {
        // Mapa en modo normal
        Box(
            modifier = modifier
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp).clip(RoundedCornerShape(10.dp)),
                cameraPositionState = cameraPositionState
            )

            // Marcador centrado
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pin_google),
                    contentDescription = "Marcador centrado",
                    modifier = Modifier.size(32.dp) // Ajustar tamaño del marcador en modo normal
                )
            }

            // Capa interactiva opcional para pantalla completa
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(enableFullScreen) {
                        if (enableFullScreen) {
                            detectTapGestures(
                                onTap = {
                                    isFullScreen = true // Activar pantalla completa
                                }
                            )
                        }
                    }
            )
        }
    }
}
