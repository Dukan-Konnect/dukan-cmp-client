package org.example.project.home.di

import org.example.project.booking.domain.repository.BookingRepository
import org.example.project.booking.data.repository.InMemoryBookingRepository
import org.example.project.profile.data.repository.InMemoryAddressRepository
import org.example.project.profile.domain.repository.AddressRepository
import org.koin.dsl.module

actual val cartPlatformModule = module {
    single<BookingRepository> { InMemoryBookingRepository() }
    single<AddressRepository> { InMemoryAddressRepository() }
}
