package org.example.project.home.domain.repository

import org.example.project.core.utils.DataState
import org.example.project.home.domain.model.Booking
import org.example.project.home.domain.model.CreateBookingRequest

interface BookingRemoteRepository {
    suspend fun createBooking(request: CreateBookingRequest): DataState<Booking>
    suspend fun getMyBookings(): DataState<List<Booking>>
}

