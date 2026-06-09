package org.example.project.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun rememberRateUsLauncher(): () -> Unit {
    return remember {
        {
            val appStoreUrl = NSURL(string = "itms-apps://")
            val webUrl = NSURL(string = "https://apps.apple.com/")

            val application = UIApplication.sharedApplication

            if (application.canOpenURL(appStoreUrl)) {
                application.openURL(appStoreUrl)
            } else {
                application.openURL(webUrl)
            }
        }
    }
}
