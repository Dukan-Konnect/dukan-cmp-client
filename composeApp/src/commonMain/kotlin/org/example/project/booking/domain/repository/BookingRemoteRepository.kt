package org.example.project.booking.domain.repository

import org.example.project.core.model.booking.Booking
import org.example.project.core.model.booking.CreateBookingRequest
import org.example.project.core.utils.DataState

interface BookingRemoteRepository {
    suspend fun createBooking(request: CreateBookingRequest): DataState<Booking>
    suspend fun getMyBookings(): DataState<List<Booking>>
    suspend fun cancelBooking(bookingId: String): DataState<Booking>
    suspend fun rescheduleBooking(bookingId: String, newScheduledDate: String): DataState<Booking>
}