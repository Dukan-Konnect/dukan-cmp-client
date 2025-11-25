package org.example.project.core.di

import org.example.project.home.domain.location.LocationPermissionHandler
import org.example.project.home.domain.location.LocationProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific core bindings.
 * Currently provides [LocationProvider] and [LocationPermissionHandler] which need an Android [Context].
 */
val coreAndroidModule = module {
    single { LocationProvider(androidContext()) }
    single { LocationPermissionHandler(androidContext()) }
}

