package org.example.project.booking.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.core.model.booking.Booking
import org.example.project.core.model.booking.BookingStatus

interface BookingRepository {
    fun observeAllBookings(): Flow<List<Booking>>
    suspend fun getBookingById(bookingId: String): Result<Booking?>
    suspend fun createBooking(booking: Booking): Result<Unit>
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit>
    suspend fun deleteBooking(bookingId: String): Result<Unit>
    suspend fun clearAllBookings(): Result<Unit>
    fun observeBookingsByStatus(status: BookingStatus): Flow<List<Booking>>
}