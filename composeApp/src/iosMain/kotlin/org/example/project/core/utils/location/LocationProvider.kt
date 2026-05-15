package org.example.project.core.utils.location

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.project.home.domain.model.UserLocation
import platform.CoreLocation.CLGeocoder

import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.CLPlacemark
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

actual class LocationProvider {

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getCurrentLocation(): UserLocation? =
        suspendCancellableCoroutine { continuation ->
            val locationManager = CLLocationManager()

            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>
                ) {
                    // 1. Immediately stop updating to prevent further hardware battery drain
                    manager.stopUpdatingLocation()

                    // 2. If the coroutine was already completed by a previous rapid-fire call, do nothing.
                    if (!continuation.isActive) return

                    val location = didUpdateLocations.lastOrNull() as? CLLocation
                    if (location != null) {
                        val lat = location.coordinate.useContents { latitude }
                        val lng = location.coordinate.useContents { longitude }

                        val geocoder = CLGeocoder()
                        geocoder.reverseGeocodeLocation(location) { placemarks, error ->
                            // 3. Double check isActive again because network geocoding takes time
                            if (continuation.isActive) {
                                if (error != null || placemarks == null || placemarks.isEmpty()) {
                                    continuation.resume(
                                        UserLocation(
                                            address = "$lat, $lng",
                                            latitude = lat,
                                            longitude = lng
                                        )
                                    )
                                } else {
                                    val placemark = placemarks.first() as CLPlacemark
                                    continuation.resume(parsePlacemark(placemark, lat, lng))
                                }
                            }
                        }
                    } else {
                        if (continuation.isActive) continuation.resume(null)
                    }
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError
                ) {
                    manager.stopUpdatingLocation()
                    if (continuation.isActive) continuation.resume(null)
                }
            }

            locationManager.delegate = delegate
            locationManager.requestLocation()

            continuation.invokeOnCancellation {
                locationManager.stopUpdatingLocation()
                locationManager.delegate = null
            }
        }

    private fun parsePlacemark(placemark: CLPlacemark, lat: Double, lng: Double): UserLocation {
        val flatNumber = placemark.subThoroughfare ?: placemark.name
        val streetAddress = placemark.thoroughfare
        val city = placemark.locality ?: placemark.subAdministrativeArea
        val state = placemark.administrativeArea
        val postalCode = placemark.postalCode
        val country = placemark.country

        val formattedParts = mutableListOf<String>()
        flatNumber?.let { if (it.isNotBlank()) formattedParts.add(it) }
        streetAddress?.let { if (it.isNotBlank()) formattedParts.add(it) }
        city?.let { if (it.isNotBlank()) formattedParts.add(it) }
        state?.let { if (it.isNotBlank()) formattedParts.add(it) }

        val formattedAddress = if (formattedParts.isNotEmpty()) {
            formattedParts.joinToString(", ")
        } else {
            "$lat, $lng"
        }

        return UserLocation(
            address = formattedAddress,
            latitude = lat,
            longitude = lng,
            flatNumber = flatNumber,
            streetAddress = streetAddress,
            city = city,
            state = state,
            postalCode = postalCode,
            country = country
        )
    }
}