package org.example.project.onboarding.presentation.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.example.project.core.utils.location.LocationPermissionHandler
import org.example.project.onboarding.presentation.viewmodel.LocationFetchIntent
import org.example.project.onboarding.presentation.viewmodel.LocationFetchViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun LocationFetchScreenWithPermissions(
    onLocationFetched: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionHandler: LocationPermissionHandler = koinInject()

    // Pass the VM instance from Koin so the Wrapper and the Screen share the exact same state
    val viewModel: LocationFetchViewModel = koinViewModel()

    val locationManager = remember { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    var hasRequestedInitialPermission by rememberSaveable { mutableStateOf(false) }

    fun isGpsEnabled() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    // The Master State Evaluator
    fun evaluateHardwareState() {
        val activity = context as? Activity ?: return

        if (!permissionHandler.hasLocationPermission()) {
            if (permissionHandler.shouldShowRequestPermissionRationale(activity)) {
                viewModel.handleIntent(LocationFetchIntent.ShowRationale)
            } else {
                viewModel.handleIntent(LocationFetchIntent.PermissionDenied)
            }
        } else if (!isGpsEnabled()) {
            viewModel.handleIntent(LocationFetchIntent.GpsDisabled)
        } else {
            // Permissions are granted AND GPS is On! Safe to fetch.
            viewModel.handleIntent(LocationFetchIntent.StartLocationFlow)
        }
    }

    // React to the app entering the foreground (e.g., returning from Settings)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val activity = context as? Activity

                // If it's the very first time opening the screen, trigger the OS permission popup automatically
                if (activity != null && !hasRequestedInitialPermission && !permissionHandler.hasLocationPermission()) {
                    hasRequestedInitialPermission = true
                    permissionHandler.requestLocationPermission(activity)
                } else {
                    // Otherwise, evaluate what state the user is currently in
                    evaluateHardwareState()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LocationFetchScreen(
        onLocationFetched = onLocationFetched,
        viewModel = viewModel,
        onOpenAppSettings = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        },
        onPromptGpsSettings = {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    )
}