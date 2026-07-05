package org.example.project.core.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.project.core.data.repository.FcmRepository
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.utils.DataState
import org.koin.compose.koinInject
import kotlin.coroutines.resume

private object NotificationPromptSession {
    var promptShownThisAppStart: Boolean = false
}

@Composable
actual fun HomeNotificationPermissionGate() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val userPreferencesRepository: UserPreferencesRepository = koinInject()
    val fcmRepository: FcmRepository = koinInject()
    val userData by userPreferencesRepository.userData.collectAsState()
    val latestUserDataState = rememberUpdatedState(userData)

    var permissionGranted by remember { mutableStateOf(hasNotificationPermission(context)) }
    val syncMutex = remember { Mutex() }

    fun requestSyncIfNeeded() {
        coroutineScope.launch {
            if (!syncMutex.tryLock()) return@launch
            try {
                val currentUser = latestUserDataState.value
                if (!currentUser.isLoggedIn) return@launch
                if (!hasNotificationPermission(context)) return@launch

                val token = resolveCurrentFcmToken(
                    currentToken = currentUser.fcmToken,
                    userPreferencesRepository = userPreferencesRepository
                ) ?: return@launch

                if (token == currentUser.syncedFcmToken) return@launch

                when (fcmRepository.syncFcmToken(token)) {
                    is DataState.Success -> {
                        userPreferencesRepository.markFcmTokenSynced(token)
                    }
                    is DataState.Error -> Unit
                    DataState.Loading -> Unit
                }
            } finally {
                syncMutex.unlock()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (granted) {
            requestSyncIfNeeded()
        }
    }

    fun maybeRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissionGranted = true
            requestSyncIfNeeded()
            return
        }

        if (!latestUserDataState.value.isLoggedIn) return
        if (permissionGranted) return
        if (NotificationPromptSession.promptShownThisAppStart) return

        NotificationPromptSession.promptShownThisAppStart = true
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    LaunchedEffect(latestUserDataState.value.isLoggedIn, permissionGranted) {
        if (latestUserDataState.value.isLoggedIn && permissionGranted) {
            requestSyncIfNeeded()
        } else if (latestUserDataState.value.isLoggedIn && !permissionGranted) {
            maybeRequestPermission()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionGranted = hasNotificationPermission(context)
                if (latestUserDataState.value.isLoggedIn && permissionGranted) {
                    requestSyncIfNeeded()
                } else if (latestUserDataState.value.isLoggedIn && !permissionGranted) {
                    maybeRequestPermission()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private fun hasNotificationPermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
}

private suspend fun resolveCurrentFcmToken(
    currentToken: String,
    userPreferencesRepository: UserPreferencesRepository
): String? {
    if (currentToken.isNotBlank()) return currentToken

    val fetchedToken = getFirebaseMessagingToken() ?: return null
    userPreferencesRepository.updateFcmToken(fetchedToken)
    return fetchedToken
}

private suspend fun getFirebaseMessagingToken(): String? = suspendCancellableCoroutine { continuation ->
    FirebaseMessaging.getInstance().token
        .addOnSuccessListener { token ->
            if (continuation.isActive) {
                continuation.resume(token)
            }
        }
        .addOnFailureListener {
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }
}
