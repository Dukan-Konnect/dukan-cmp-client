package org.example.project.home.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.home.data.local.dao.BookingDao
import org.example.project.home.data.local.mappers.toDomain
import org.example.project.home.data.local.mappers.toEntity
import org.example.project.core.model.booking.Booking
import org.example.project.core.model.booking.BookingStatus
import org.example.project.home.domain.repository.BookingRepository

class BookingRepositoryImpl(
    private val bookingDao: BookingDao
) : BookingRepository {

    override fun observeAllBookings(): Flow<List<Booking>> {
        return bookingDao.observeAllBookings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getBookingById(bookingId: String): Result<Booking?> {
        return try {
            val booking = bookingDao.getBookingById(bookingId)?.toDomain()
            Result.success(booking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBooking(booking: Booking): Result<Unit> {
        return try {
            bookingDao.insertBooking(booking.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBookingStatus(
        bookingId: String,
        status: BookingStatus
    ): Result<Unit> {
        return try {
            val booking = bookingDao.getBookingById(bookingId)
            if (booking != null) {
                val updatedBooking = booking.copy(status = status.name)
                bookingDao.updateBooking(updatedBooking)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Booking not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBooking(bookingId: String): Result<Unit> {
        return try {
            bookingDao.deleteBooking(bookingId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearAllBookings(): Result<Unit> {
        return try {
            bookingDao.deleteAllBookings()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeBookingsByStatus(status: BookingStatus): Flow<List<Booking>> {
        return bookingDao.observeBookingsByStatus(status.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}

