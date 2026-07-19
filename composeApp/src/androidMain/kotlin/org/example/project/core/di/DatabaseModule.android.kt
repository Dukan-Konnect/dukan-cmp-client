package org.example.project.core.di

import android.content.Context
import org.koin.dsl.module
import org.example.project.home.data.local.database.createAppDatabase

actual val databasePlatformModule = module {
    // Android-specific database creation
    // Use get() to retrieve Context from Koin container at runtime
    single { createAppDatabase(get<Context>()) }
}
