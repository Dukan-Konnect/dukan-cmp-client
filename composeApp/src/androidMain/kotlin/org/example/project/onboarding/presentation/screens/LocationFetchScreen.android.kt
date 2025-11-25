package org.example.project.onboarding.presentation.screens

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import org.example.project.home.domain.location.LocationPermissionHandler
import org.koin.compose.koinInject

/**
 * Android-specific location permission request wrapper for LocationFetchScreen.
 * This ensures permissions are requested before the ViewModel tries to fetch location.
 */
@Composable
actual fun LocationFetchScreenWithPermissions(
    onLocationFetched: () -> Unit
) {
    val context = LocalContext.current
    val permissionHandler: LocationPermissionHandler = koinInject()

    // Request permission when screen is first composed
    LaunchedEffect(Unit) {
        val activity = context as? Activity
        if (activity != null && !permissionHandler.hasLocationPermission()) {
            permissionHandler.requestLocationPermission(activity)
        }
    }

    // Show the actual location fetch screen
    LocationFetchScreen(onLocationFetched = onLocationFetched)
}

