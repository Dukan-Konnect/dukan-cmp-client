package org.example.project.home.domain.location

import org.example.project.home.domain.model.UserLocation

/**
 * Expect/actual abstraction for getting the current user location.
 * Android will provide a real implementation; other platforms can add later.
 */
expect class LocationProvider {
    suspend fun getCurrentLocation(): UserLocation?
}

