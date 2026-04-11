package org.example.project.home.domain.location

import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted

/**
 * iOS actual implementation of LocationPermissionHandler.
 * No Context or Activity is needed on iOS.
 */
class IosLocationPermissionHandler : LocationPermissionHandler {

    // Must be a class property so the OS doesn't garbage collect it during the popup!
    private val locationManager = CLLocationManager()

    override fun hasLocationPermission(): Boolean {
        val status = locationManager.authorizationStatus
        return status == kCLAuthorizationStatusAuthorizedWhenInUse ||
                status == kCLAuthorizationStatusAuthorizedAlways
    }

    override fun requestLocationPermission(activity: Any?): Boolean {
        if (hasLocationPermission()) return true

        val status = locationManager.authorizationStatus
        if (status == kCLAuthorizationStatusNotDetermined) {
            // Triggers the native iOS permission popup
            locationManager.requestWhenInUseAuthorization()
        }
        return false
    }

    override fun isPermanentlyDenied(): Boolean {
        val status = locationManager.authorizationStatus
        // iOS specific: Checks if the user explicitly clicked "Don't Allow"
        return status == kCLAuthorizationStatusDenied ||
                status == kCLAuthorizationStatusRestricted
    }
}