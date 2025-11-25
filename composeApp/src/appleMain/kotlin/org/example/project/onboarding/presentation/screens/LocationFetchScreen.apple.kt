package org.example.project.onboarding.presentation.screens

import androidx.compose.runtime.Composable

/**
 * Apple (iOS/macOS) location permission request wrapper for LocationFetchScreen.
 * TODO: Implement iOS location authorization handling using CLLocationManager.
 */
@Composable
actual fun LocationFetchScreenWithPermissions(
    onLocationFetched: () -> Unit
) {
    // TODO: Request iOS location permissions using CLLocationManager
    // For now, just show the screen
    LocationFetchScreen(onLocationFetched = onLocationFetched)
}

