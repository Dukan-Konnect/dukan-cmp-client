package org.example.project.onboarding.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.example.project.onboarding.presentation.viewmodel.LocationFetchIntent
import org.example.project.onboarding.presentation.viewmodel.LocationFetchViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import platform.CoreLocation.CLLocationManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@Composable
actual fun LocationFetchScreenWithPermissions(
    onLocationFetched: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionHandler: LocationPermissionHandler = koinInject()
    val viewModel: LocationFetchViewModel = koinViewModel()

    var hasRequestedInitialPermission by remember { mutableStateOf(false) }

    // Check if system-wide Location Services are enabled
    fun isGpsEnabled() = CLLocationManager.locationServicesEnabled()

    fun evaluateHardwareState() {
        if (!permissionHandler.hasLocationPermission()) {
            if (permissionHandler.isPermanentlyDenied()) {
                viewModel.handleIntent(LocationFetchIntent.PermissionDenied)
            }
            // If it's NotDetermined, we do nothing and let the popup handle it
        } else if (!isGpsEnabled()) {
            viewModel.handleIntent(LocationFetchIntent.GpsDisabled)
        } else {
            // Perms granted, GPS on
            viewModel.handleIntent(LocationFetchIntent.StartLocationFlow)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (!hasRequestedInitialPermission && !permissionHandler.hasLocationPermission()) {
                    hasRequestedInitialPermission = true
                    // Triggers the iOS prompt. Pass null since iOS doesn't need an Activity.
                    permissionHandler.requestLocationPermission(null)
                } else {
                    evaluateHardwareState()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // A helper function to deep-link into the iOS Settings app
    val openIosSettings = {
        val url = NSURL(string = UIApplicationOpenSettingsURLString)
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }

    LocationFetchScreen(
        onLocationFetched = onLocationFetched,
        viewModel = viewModel,
        onOpenAppSettings = { openIosSettings() },
        onPromptGpsSettings = {
            // iOS doesn't have a direct deep-link to the global GPS toggle.
            // The standard Apple practice is to send them to the App Settings.
            openIosSettings()
        }
    )
}