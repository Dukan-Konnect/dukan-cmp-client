package org.example.project.home.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.home.domain.model.Booking
import org.example.project.home.domain.model.BookingStatus

interface BookingRepository {
    fun observeAllBookings(): Flow<List<Booking>>
    suspend fun getBookingById(bookingId: String): Result<Booking?>
    suspend fun createBooking(booking: Booking): Result<Unit>
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit>
    suspend fun deleteBooking(bookingId: String): Result<Unit>
    fun observeBookingsByStatus(status: BookingStatus): Flow<List<Booking>>
}

