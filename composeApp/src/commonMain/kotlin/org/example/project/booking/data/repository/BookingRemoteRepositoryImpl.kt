package org.example.project.booking.data.repository

import org.example.project.booking.domain.dto.RescheduleRequestDto
import org.example.project.booking.domain.repository.BookingRemoteRepository
import org.example.project.core.model.booking.Booking
import org.example.project.core.model.booking.CreateBookingRequest
import org.example.project.core.network.dto.booking.CreateBookingRequestDto
import org.example.project.core.network.services.BookingService
import org.example.project.core.utils.DataState
import org.example.project.core.utils.safeApiCall
import org.example.project.home.data.mapper.toDomain

class BookingRemoteRepositoryImpl(
    private val bookingService: BookingService
) : BookingRemoteRepository {

    override suspend fun createBooking(request: CreateBookingRequest): DataState<Booking> =
        safeApiCall {
            bookingService.createBooking(
                CreateBookingRequestDto(
                    subServiceId = request.subServiceId,
                    providerId = request.providerId,
                    scheduledDate = request.scheduledDate,
                    address = request.address
                )
            ).toDomain()
        }

    override suspend fun getMyBookings(): DataState<List<Booking>> = safeApiCall {
        bookingService.getMyBookings().map { it.toDomain() }
    }

    override suspend fun cancelBooking(bookingId: String): DataState<Booking> = safeApiCall {
        bookingService.cancelBooking(bookingId).toDomain()
    }

    override suspend fun rescheduleBooking(bookingId: String, newScheduledDate: String): DataState<Booking> = safeApiCall {
        bookingService.rescheduleBooking(
            id = bookingId,
            request = RescheduleRequestDto(newScheduledDate)
        ).toDomain()
    }
}