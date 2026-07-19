package org.example.project.core.utils.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
private class MapDelegate(
    val onCameraPositionChanged: (Double, Double) -> Unit
) : NSObject(), MKMapViewDelegateProtocol {
    override fun mapView(mapView: MKMapView, regionDidChangeAnimated: Boolean) {
        mapView.centerCoordinate.useContents {
            onCameraPositionChanged(latitude, longitude)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    onCameraPositionChanged: (Double, Double) -> Unit
) {
    val mapDelegate = remember { MapDelegate(onCameraPositionChanged) }
    
    val mapView = remember {
        MKMapView().apply {
            delegate = mapDelegate
        }
    }

    LaunchedEffect(latitude, longitude) {
        mapView.centerCoordinate.useContents {
            if (kotlin.math.abs(this.latitude - latitude) > 0.00001 || 
                kotlin.math.abs(this.longitude - longitude) > 0.00001) {
                val center = CLLocationCoordinate2DMake(latitude, longitude)
                val region = MKCoordinateRegionMakeWithDistance(center, 1000.0, 1000.0)
                mapView.setRegion(region, animated = true)
            }
        }
    }

    UIKitView(
        factory = { mapView },
        modifier = modifier
    )
}
