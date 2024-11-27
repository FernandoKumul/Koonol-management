package com.fernandokh.koonol_management.ui.components.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapComponent(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Float = 15f,
    markerTitle: String = "Ubicación"
) {
    // Coordenadas del marcador
    val markerPosition = LatLng(latitude, longitude)

    // Configuración de la posición de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, zoom)
    }

    // Estado del marcador
    val markerState = rememberMarkerState(position = markerPosition)

    // Renderizar el mapa
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        // Agregar un marcador en el mapa
        Marker(
            state = markerState,
            title = markerTitle,
            snippet = "Esta es la ubicación del tianguis"
        )
    }
}

