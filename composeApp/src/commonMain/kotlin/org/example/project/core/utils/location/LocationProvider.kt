package org.example.project.core.utils.location

import org.example.project.core.model.location.UserLocation

/**
 * Expect/actual abstraction for getting the current user location.
 * Android will provide a real implementation; other platforms can add later.
 */
expect class LocationProvider {
    suspend fun getCurrentLocation(): UserLocation?
    suspend fun getAddressFromLocation(latitude: Double, longitude: Double): UserLocation?
}