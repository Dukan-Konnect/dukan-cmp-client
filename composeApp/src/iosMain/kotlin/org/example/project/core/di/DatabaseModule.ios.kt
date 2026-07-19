package org.example.project.core.di

import org.example.project.home.data.local.database.createAppDatabase
import org.koin.dsl.module

actual val databasePlatformModule = module {
    single { createAppDatabase() }
}
