package org.example.project.home.di

import android.content.Context
import org.koin.dsl.module
import org.example.project.home.data.local.database.createCartDatabase

actual val cartPlatformModule = module {
    // Android-specific database creation
    // Use get() to retrieve Context from Koin container at runtime
    single { createCartDatabase(get<Context>()) }
}
