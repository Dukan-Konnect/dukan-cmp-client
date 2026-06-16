package org.example.project.core.network.services

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import org.example.project.booking.domain.dto.RescheduleRequestDto
import org.example.project.core.network.dto.booking.BookingResponseDto
import org.example.project.core.network.dto.booking.CreateBookingRequestDto
import org.example.project.core.utils.ApiEndPoints

interface BookingService {

    @POST(ApiEndPoints.BOOKINGS)
    suspend fun createBooking(
        @Body request: CreateBookingRequestDto
    ): BookingResponseDto

    @GET(ApiEndPoints.BOOKINGS)
    suspend fun getMyBookings(
    ): List<BookingResponseDto>

    @POST(ApiEndPoints.BOOKINGS + "/{id}/cancel")
    suspend fun cancelBooking(
        @Path("id") id: String
    ): BookingResponseDto

    @PUT(ApiEndPoints.BOOKINGS + "/{id}/reschedule")
    suspend fun rescheduleBooking(
        @Path("id") id: String,
        @Body request: RescheduleRequestDto
    ): BookingResponseDto
}

