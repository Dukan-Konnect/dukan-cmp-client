package org.example.project

import android.app.Application
import org.example.project.core.config.RazorpayConfig
import org.example.project.core.di.coreAndroidModule
import org.example.project.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class DukaanKonnectApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@DukaanKonnectApp)
            properties(
                mapOf(
                    "razorpay.key.id" to RazorpayConfig.KEY_ID,
                    "razorpay.key.secret" to RazorpayConfig.KEY_SECRET
                )
            )
            modules(appModules, coreAndroidModule)
        }
    }
}
