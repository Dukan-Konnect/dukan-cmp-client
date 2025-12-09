package org.example.project.home.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.example.project.home.data.local.database.CartDatabase
import org.example.project.home.data.repository.BookingRepositoryImpl
import org.example.project.home.data.repository.CartRepositoryImpl
import org.example.project.home.domain.repository.BookingRepository
import org.example.project.home.domain.repository.CartRepository
import org.example.project.home.domain.usecase.CartUseCases
import org.example.project.home.presentation.viewmodels.BookingsViewModel
import org.example.project.home.presentation.viewmodels.ProfileViewModel

expect val cartPlatformModule: org.koin.core.module.Module

val cartModule = module {
    includes(cartPlatformModule)

    // DAOs
    single { get<CartDatabase>().cartDao() }
    single { get<CartDatabase>().bookingDao() }

    // Repositories
    single<CartRepository> { CartRepositoryImpl(get()) }
    single<BookingRepository> { BookingRepositoryImpl(get()) }

    // Use Cases
    single { CartUseCases(get()) }

    // ViewModels
    viewModelOf(::BookingsViewModel)
    viewModelOf(::ProfileViewModel)
}

