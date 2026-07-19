package org.example.project.core.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.example.project.core.data.database.AppDatabase
import org.example.project.booking.data.repository.BookingRepositoryImpl
import org.example.project.booking.data.repository.BookingRemoteRepositoryImpl
import org.example.project.booking.domain.repository.BookingRepository
import org.example.project.booking.domain.repository.BookingRemoteRepository
import org.example.project.booking.presentation.viewmodels.BookingsViewModel
import org.example.project.profile.presentation.viewmodels.ProfileViewModel
import org.example.project.profile.data.repository.AddressRepositoryImpl
import org.example.project.profile.domain.repository.AddressRepository
import org.koin.core.module.Module

expect val databasePlatformModule: Module

val databaseModule = module {
    includes(databasePlatformModule)

    // DAOs
    single { get<AppDatabase>().bookingDao() }
    single { get<AppDatabase>().addressDao() }

    // Repositories
    single<BookingRepository> { BookingRepositoryImpl(get()) }
    single<BookingRemoteRepository> { BookingRemoteRepositoryImpl(get(), get()) }
    single<AddressRepository> { AddressRepositoryImpl(get()) }

    // ViewModels
    viewModelOf(::BookingsViewModel)
    viewModelOf(::ProfileViewModel)
}
