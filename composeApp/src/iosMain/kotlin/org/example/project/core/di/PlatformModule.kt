package org.example.project.core.di

import org.example.project.core.network.IosNetworkMonitor
import org.example.project.core.network.NetworkMonitor
import org.example.project.core.utils.location.LocationProvider
import org.example.project.core.utils.location.LocationPermissionHandler
import org.koin.dsl.module

val platformModule = module {
    single { LocationProvider() }
    single { LocationPermissionHandler() }
    single<NetworkMonitor> { IosNetworkMonitor() }
}
