package org.example.project.home.domain.location

import kotlinx.cinterop.ExperimentalForeignApi
import platform.darwin.NSObject

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.*
import platform.Foundation.NSError
import org.example.project.home.domain.model.UserLocation
import kotlin.coroutines.resume

actual class LocationProvider {

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getCurrentLocation(): UserLocation? = suspendCancellableCoroutine { continuation ->
        val locationManager = CLLocationManager()

        // Create an Objective-C Delegate to receive the location updates
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.lastOrNull() as? CLLocation
                if (location != null) {
                    manager.stopUpdatingLocation()

                    // 1. We got the coordinates, now perform Reverse Geocoding
                    val geocoder = CLGeocoder()
                    geocoder.reverseGeocodeLocation(location) { placemarks, error ->
                        if (error != null || placemarks == null || placemarks.isEmpty()) {
                            // Fallback to raw coordinates if geocoding fails
                            continuation.resume(
                                UserLocation(
                                    address = "${location.coordinate.latitude}, ${location.coordinate.longitude}",
                                    latitude = location.coordinate.latitude,
                                    longitude = location.coordinate.longitude
                                )
                            )
                        } else {
                            val placemark = placemarks.first() as CLPlacemark
                            continuation.resume(parsePlacemark(placemark, location.coordinate.latitude, location.coordinate.longitude))
                        }
                    }
                } else {
                    continuation.resume(null)
                }
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                manager.stopUpdatingLocation()
                continuation.resume(null)
            }
        }

        // Retain the delegate strongly so Kotlin Native doesn't garbage collect it
        locationManager.delegate = delegate
        locationManager.requestLocation() // Request a single location fix

        // Cleanup if the coroutine is cancelled
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