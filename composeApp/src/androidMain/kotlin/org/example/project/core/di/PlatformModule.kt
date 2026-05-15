package org.example.project.core.di

import org.example.project.core.utils.location.LocationPermissionHandler
import org.example.project.core.utils.location.LocationProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific core bindings.
 * Currently provides [LocationProvider] and [LocationPermissionHandler] which need an Android [Context].
 */
val platformModule = module {
    single { LocationProvider(androidContext()) }
    single { LocationPermissionHandler(androidContext()) }
}

