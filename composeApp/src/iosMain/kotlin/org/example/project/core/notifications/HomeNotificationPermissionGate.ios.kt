package org.example.project.core.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.project.core.datastore.UserPreferencesRepository
import org.koin.compose.koinInject
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

private object NotificationPromptSession {
    var promptShownThisAppStart: Boolean = false
}

@Composable
actual fun HomeNotificationPermissionGate() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val userPreferencesRepository: UserPreferencesRepository = koinInject()
    val userData by userPreferencesRepository.userData.collectAsState()
    val latestUserData = rememberUpdatedState(userData)

    var permissionGranted by remember { mutableStateOf(false) }

    suspend fun refreshPermissionAndRegister() {
        permissionGranted = queryNotificationPermissionGranted()
    }

    fun requestPermissionIfNeeded() {
        coroutineScope.launch {
            if (!latestUserData.value.isLoggedIn) return@launch
            if (permissionGranted) return@launch
            if (NotificationPromptSession.promptShownThisAppStart) return@launch

            NotificationPromptSession.promptShownThisAppStart = true

            val granted = requestNotificationPermission()
            permissionGranted = granted
        }
    }

    LaunchedEffect(latestUserData.value.isLoggedIn) {
        refreshPermissionAndRegister()
        if (latestUserData.value.isLoggedIn && !permissionGranted) {
            requestPermissionIfNeeded()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch {
                    refreshPermissionAndRegister()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private suspend fun queryNotificationPermissionGranted(): Boolean = suspendCancellableCoroutine { continuation ->
    val center = UNUserNotificationCenter.currentNotificationCenter()
    center.getNotificationSettingsWithCompletionHandler { settings ->
        val status = settings?.authorizationStatus
        val granted = when (status) {
            UNAuthorizationStatusAuthorized,
            UNAuthorizationStatusProvisional,
            UNAuthorizationStatusEphemeral -> true
            UNAuthorizationStatusNotDetermined -> false
            else -> false
        }
        if (continuation.isActive) {
            continuation.resume(granted)
        }
    }
}

private suspend fun requestNotificationPermission(): Boolean = suspendCancellableCoroutine { continuation ->
    val options = UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound
    UNUserNotificationCenter.currentNotificationCenter()
        .requestAuthorizationWithOptions(options) { granted, _ ->
            if (granted) {
                platform.darwin.dispatch_async(platform.darwin.dispatch_get_main_queue()) {
                    platform.Foundation.NSNotificationCenter.defaultCenter.postNotificationName(
                        "RequestPushRegistration", 
                        null
                    )
                }
            }
            if (continuation.isActive) {
                continuation.resume(granted)
            }
        }
}
