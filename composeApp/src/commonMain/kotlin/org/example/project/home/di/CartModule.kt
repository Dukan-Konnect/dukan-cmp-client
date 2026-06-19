package org.example.project.home.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.example.project.home.data.local.database.CartDatabase
import org.example.project.booking.data.repository.BookingRepositoryImpl
import org.example.project.booking.data.repository.BookingRemoteRepositoryImpl
import org.example.project.booking.domain.repository.BookingRepository
import org.example.project.booking.domain.repository.BookingRemoteRepository
import org.example.project.booking.presentation.viewmodels.BookingsViewModel
import org.example.project.profile.presentation.viewmodels.ProfileViewModel
import org.example.project.profile.data.repository.AddressRepositoryImpl
import org.example.project.profile.domain.repository.AddressRepository

expect val cartPlatformModule: org.koin.core.module.Module

val cartModule = module {
    includes(cartPlatformModule)

    // DAOs
    single { get<CartDatabase>().bookingDao() }
    single { get<CartDatabase>().addressDao() }

    // Repositories
    single<BookingRepository> { BookingRepositoryImpl(get()) }
    single<BookingRemoteRepository> { BookingRemoteRepositoryImpl(get(), get()) }
    single<AddressRepository> { AddressRepositoryImpl(get()) }

    // ViewModels
    viewModelOf(::BookingsViewModel)
    viewModelOf(::ProfileViewModel)
}
