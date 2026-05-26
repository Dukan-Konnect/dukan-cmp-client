package org.example.project.core.network.services

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import org.example.project.core.network.dto.booking.BookingResponseDto
import org.example.project.core.network.dto.booking.CreateBookingRequestDto
import org.example.project.core.utils.ApiEndPoints

interface BookingService {
    @POST(ApiEndPoints.BOOKINGS)
    suspend fun createBooking(
        @Body request: CreateBookingRequestDto
    ): BookingResponseDto

    @GET(ApiEndPoints.BOOKINGS)
    suspend fun getMyBookings(): List<BookingResponseDto>
}

