package org.example.project.core.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.example.project.MainActivity
import org.example.project.R
import org.example.project.core.data.repository.FcmRepository
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.utils.DataState
import org.koin.core.context.GlobalContext

class DukanFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Deprecated("Deprecated in Java")
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        serviceScope.launch {
            val preferencesRepository = getPreferencesRepository()
            preferencesRepository.updateFcmToken(token)

            if (shouldSyncToken(this@DukanFirebaseMessagingService, preferencesRepository)) {
                syncToken(token, preferencesRepository)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: getString(R.string.app_name)
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["message"]
            ?: ""

        if (title.isBlank() && body.isBlank()) return

        showLocalNotification(
            context = this,
            title = title,
            body = body
        )
    }

    private suspend fun syncToken(
        token: String,
        preferencesRepository: UserPreferencesRepository
    ) {
        val fcmRepository = getFcmRepository()

        when (fcmRepository.syncFcmToken(token)) {
            is DataState.Success -> {
                preferencesRepository.markFcmTokenSynced(token)
            }
            is DataState.Error -> Unit
            DataState.Loading -> Unit
        }
    }

    private fun shouldSyncToken(
        context: Context,
        preferencesRepository: UserPreferencesRepository
    ): Boolean {
        val userData = preferencesRepository.userData.value
        return userData.isLoggedIn &&
            hasNotificationPermission(context) &&
            userData.fcmToken.isNotBlank() &&
            userData.fcmToken != userData.syncedFcmToken
    }

    private fun getPreferencesRepository(): UserPreferencesRepository {
        return GlobalContext.get().get()
    }

    private fun getFcmRepository(): FcmRepository {
        return GlobalContext.get().get()
    }
}

private const val NOTIFICATION_CHANNEL_ID = "dukankonnect_notifications"
private const val NOTIFICATION_CHANNEL_NAME = "DukanKonnect Notifications"

private fun hasNotificationPermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
}

@SuppressLint("MissingPermission")
private fun showLocalNotification(
    context: Context,
    title: String,
    body: String
) {
    if (!hasNotificationPermission(context)) return

    ensureNotificationChannel(context)

    val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        ?: Intent(context, MainActivity::class.java)

    val pendingIntent = PendingIntent.getActivity(
        context,
        title.hashCode(),
        launchIntent.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(context.applicationInfo.icon)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    NotificationManagerCompat.from(context).notify(title.hashCode(), notification)
}

private fun ensureNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val notificationManager = context.getSystemService(NotificationManager::class.java) ?: return
    val channel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
    )
    notificationManager.createNotificationChannel(channel)
}
