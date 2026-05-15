package org.example.project

import android.app.Application
import org.example.project.core.di.platformModule
import org.example.project.core.utils.initializeKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class DukaanKonnectApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initializeKoin(
            additionalModules = listOf(platformModule)
        ) {
            androidLogger(Level.ERROR)
            androidContext(this@DukaanKonnectApp)
        }
    }
}
