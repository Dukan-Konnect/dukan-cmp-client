package org.example.project.core.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
actual fun rememberRateUsLauncher(): () -> Unit {
    val context = LocalContext.current

    return remember(context) {
        {
            val playStorePackage = "com.android.vending"
            val marketIntent = context.packageManager.getLaunchIntentForPackage(playStorePackage)

            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps".toUri()
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (marketIntent != null) {
                try {
                    context.startActivity(marketIntent)
                } catch (_: ActivityNotFoundException) {
                    context.startActivity(webIntent)
                }
            } else {
                try {
                    context.startActivity(webIntent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}