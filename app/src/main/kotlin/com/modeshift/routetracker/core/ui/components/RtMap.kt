package com.modeshift.routetracker.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun RtMap(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    onMapLoaded: (() -> Unit)? = null,
    onMapClick: ((LatLng) -> Unit)? = null,
    content: @Composable @GoogleMapComposable () -> Unit = {},
) {
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            scrollGesturesEnabled = true,
            myLocationButtonEnabled = true,
            mapToolbarEnabled = true,
            compassEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabledDuringRotateOrZoom = true,
            tiltGesturesEnabled = false,
        )
    }
    GoogleMap(
        modifier = modifier,
        properties = MapProperties(
            isMyLocationEnabled = true,
        ),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
        onMapLoaded = onMapLoaded,
        onMapClick = onMapClick,
        content = content
    )
}