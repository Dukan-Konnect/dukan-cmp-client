package org.example.project.booking.data.mapper

import org.example.project.core.network.dto.booking.BookingResponseDto
import org.example.project.core.model.booking.Booking
import org.example.project.core.model.booking.BookingStatus
import org.example.project.core.model.booking.PaymentStatus

fun BookingResponseDto.toDomain(): Booking {
    val bookingStatus = runCatching { BookingStatus.valueOf(status ?: BookingStatus.PENDING.name) }
        .getOrDefault(BookingStatus.PENDING)
    val payment = runCatching { PaymentStatus.valueOf(paymentStatus ?: PaymentStatus.PENDING.name) }
        .getOrDefault(PaymentStatus.PENDING)

    return Booking(
        id = id,
        orderId = orderId,
        subServiceId = subServiceId,
        subServiceName = subServiceName,
        subServiceImage = subServiceImage,
        providerId = providerId,
        providerName = providerName,
        providerImage = providerImage,
        providerPhone = providerPhone,
        providerRating = providerRating ?: 0.0,
        providerFee = providerFeeCents ?: 0L,
        servicePriceCents = servicePriceCents ?: 0L,
        totalAmountCents = totalAmountCents ?: 0L,
        bookingDate = bookingDate ?: 0L,
        scheduledDate = scheduledDate,
        address = address,
        status = bookingStatus,
        paymentStatus = payment
    )
}

