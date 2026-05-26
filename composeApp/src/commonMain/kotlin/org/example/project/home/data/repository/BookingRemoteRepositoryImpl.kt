package org.example.project.home.data.repository

import org.example.project.core.network.dto.booking.CreateBookingRequestDto
import org.example.project.core.network.services.BookingService
import org.example.project.core.utils.DataState
import org.example.project.core.utils.safeApiCall
import org.example.project.home.data.mapper.toDomain
import org.example.project.home.domain.model.Booking
import org.example.project.home.domain.model.CreateBookingRequest
import org.example.project.home.domain.repository.BookingRemoteRepository

class BookingRemoteRepositoryImpl(
    private val bookingService: BookingService
) : BookingRemoteRepository {

    override suspend fun createBooking(request: CreateBookingRequest): DataState<Booking> = safeApiCall {
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
}

