package org.example.project.home.di

import org.example.project.home.data.local.database.createCartDatabase
import org.koin.dsl.module

actual val cartPlatformModule = module {
    single { createCartDatabase() }
}
