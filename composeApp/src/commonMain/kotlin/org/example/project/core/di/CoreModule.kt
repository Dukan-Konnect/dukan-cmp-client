package org.example.project.core.di

import org.example.project.core.settings.AuthSettings
import org.koin.dsl.module

val coreModule = module {
    single { AuthSettings() }
}

