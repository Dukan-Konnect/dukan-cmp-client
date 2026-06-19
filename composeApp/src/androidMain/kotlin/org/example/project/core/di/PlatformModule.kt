package org.example.project.core.di

import org.example.project.core.network.AndroidNetworkMonitor
import org.example.project.core.network.NetworkMonitor
import org.example.project.core.utils.location.LocationPermissionHandler
import org.example.project.core.utils.location.LocationProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val platformModule = module {
    single { LocationProvider(androidContext()) }
    single { LocationPermissionHandler(androidContext()) }
    single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }
}