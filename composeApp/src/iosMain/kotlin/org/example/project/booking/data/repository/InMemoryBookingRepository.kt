package org.example.project.booking.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.example.project.booking.domain.repository.BookingRepository
import org.example.project.core.model.booking.Booking
import org.example.project.core.model.booking.BookingStatus

class InMemoryBookingRepository : BookingRepository {
    private val bookingsFlow = MutableStateFlow<List<Booking>>(emptyList())

    override fun observeAllBookings(): Flow<List<Booking>> = bookingsFlow.asStateFlow()

    override suspend fun getBookingById(bookingId: String): Result<Booking?> = runCatching {
        bookingsFlow.value.firstOrNull { it.id == bookingId }
    }

    override suspend fun createBooking(booking: Booking): Result<Unit> = runCatching {
        bookingsFlow.update { current ->
            val withoutExisting = current.filterNot { it.id == booking.id }
            withoutExisting + booking
        }
    }

    override suspend fun updateBookingStatus(
        bookingId: String,
        status: BookingStatus
    ): Result<Unit> = runCatching {
        bookingsFlow.update { current ->
            current.map { booking ->
                if (booking.id == bookingId) booking.copy(status = status) else booking
            }
        }
    }

    override suspend fun deleteBooking(bookingId: String): Result<Unit> = runCatching {
        bookingsFlow.update { current -> current.filterNot { it.id == bookingId } }
    }

    override suspend fun clearAllBookings(): Result<Unit> = runCatching {
        bookingsFlow.value = emptyList()
    }

    override fun observeBookingsByStatus(status: BookingStatus): Flow<List<Booking>> {
        return bookingsFlow.map { bookings ->
            bookings.filter { it.status == status }
        }
    }
}
