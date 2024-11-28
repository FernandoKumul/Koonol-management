package com.fernandokh.koonol_management.ui.components.maps

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapComponent(
    modifier: Modifier = Modifier,
    initialLatitude: Double,
    initialLongitude: Double,
    zoom: Float = 15f, // Nivel de zoom inicial
    markerTitle: String = "Ubicación",
    isDraggable: Boolean = false, // Determina si el marcador es arrastrable
    onMarkerDragEnd: ((LatLng) -> Unit)? = null // Callback opcional para manejar cambios de posición
) {
    // Estado del marcador inicial
    var markerPosition by remember { mutableStateOf(LatLng(initialLatitude, initialLongitude)) }

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
        // Agregar marcador
        Marker(
            state = markerState,
            title = markerTitle,
            snippet = "Arrastra el marcador para cambiar la ubicación",
            draggable = isDraggable, // Permitir arrastre si está habilitado
            onClick = {
                // Opcional: Manejo de clic en el marcador
                false
            }
        )
    }

    // Detectar arrastre y ejecutar el callback al finalizar
    LaunchedEffect(markerState.dragState) {
        if (markerState.dragState == com.google.maps.android.compose.DragState.END) {
            markerPosition = markerState.position // Actualizar la posición del marcador
            onMarkerDragEnd?.invoke(markerState.position) // Llamar al callback con la nueva posición
        }
    }
}
