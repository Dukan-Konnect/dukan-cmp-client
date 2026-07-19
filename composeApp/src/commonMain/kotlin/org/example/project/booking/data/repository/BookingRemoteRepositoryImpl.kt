package org.example.project.booking.data.repository

import org.example.project.booking.domain.dto.RescheduleRequestDto
import org.example.project.booking.domain.repository.BookingRemoteRepository
import org.example.project.core.model.booking.Booking
import org.example.project.core.model.booking.CreateBookingRequest
import org.example.project.core.network.dto.booking.CreateBookingRequestDto
import org.example.project.core.network.services.BookingService
import org.example.project.core.utils.ApiCallHelper
import org.example.project.core.utils.DataState
import org.example.project.booking.data.mapper.toDomain

class BookingRemoteRepositoryImpl(
    private val bookingService: BookingService,
    private val apiCallHelper: ApiCallHelper
) : BookingRemoteRepository {

    override suspend fun createBooking(request: CreateBookingRequest): DataState<Booking> =
        apiCallHelper.execute {
            bookingService.createBooking(
                CreateBookingRequestDto(
                    subServiceId = request.subServiceId,
                    providerId = request.providerId,
                    scheduledDate = request.scheduledDate,
                    address = request.address
                )
            ).toDomain()
        }

    override suspend fun getMyBookings(): DataState<List<Booking>> = apiCallHelper.execute {
        bookingService.getMyBookings().map { it.toDomain() }
    }

    override suspend fun cancelBooking(bookingId: String): DataState<Booking> = apiCallHelper.execute {
        bookingService.cancelBooking(bookingId).toDomain()
    }

    override suspend fun rescheduleBooking(bookingId: String, newScheduledDate: String): DataState<Booking> = apiCallHelper.execute {
        bookingService.rescheduleBooking(
            id = bookingId,
            request = RescheduleRequestDto(newScheduledDate)
        ).toDomain()
    }
}