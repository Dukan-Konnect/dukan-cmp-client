package org.example.project.home.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.example.project.home.data.local.database.createCartDatabase

actual val cartPlatformModule = module {
    // Android-specific database creation
    single { createCartDatabase(androidContext()) }
}
