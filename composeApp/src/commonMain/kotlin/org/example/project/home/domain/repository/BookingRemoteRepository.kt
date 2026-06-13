package org.example.project.home.domain.repository

import org.example.project.core.utils.DataState
import org.example.project.core.model.booking.Booking
import org.example.project.core.model.booking.CreateBookingRequest

interface BookingRemoteRepository {
    suspend fun createBooking(request: CreateBookingRequest): DataState<Booking>
    suspend fun getMyBookings(): DataState<List<Booking>>
}

