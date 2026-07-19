package org.example.project.core.utils.location

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.rememberMapViewWithLifecycle

@Composable
actual fun MapView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    onCameraPositionChanged: (Double, Double) -> Unit
) {
    val mapView = rememberMapViewWithLifecycle()

    AndroidView(
        factory = { 
            mapView.apply {
                getMapAsync { map ->
                    map.setStyle(Style.Builder().fromUri("https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json"))
                    map.addOnCameraIdleListener {
                        val target = map.cameraPosition.target
                        if (target != null) {
                            onCameraPositionChanged(target.latitude, target.longitude)
                        }
                    }
                }
            }
        },
        modifier = modifier
    )

    LaunchedEffect(latitude, longitude) {
        mapView.getMapAsync { map ->
            val currentTarget = map.cameraPosition.target
            if (currentTarget == null || 
                kotlin.math.abs(currentTarget.latitude - latitude) > 0.00001 || 
                kotlin.math.abs(currentTarget.longitude - longitude) > 0.00001) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15.0))
            }
        }
    }
}
