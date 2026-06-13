package org.example.project.home.data.local.mappers

import org.example.project.home.data.local.entities.BookingEntity
import org.example.project.core.model.booking.Booking
import org.example.project.core.model.booking.BookingStatus
import org.example.project.core.model.booking.PaymentStatus

fun BookingEntity.toDomain(): Booking {
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
        providerRating = providerRating,
        providerFee = providerFeeCents,
        servicePriceCents = servicePriceCents,
        totalAmountCents = totalAmountCents,
        bookingDate = bookingDate,
        scheduledDate = scheduledDate,
        address = address,
        status = BookingStatus.valueOf(status),
        paymentStatus = PaymentStatus.valueOf(paymentStatus)
    )
}

fun Booking.toEntity(): BookingEntity {
    return BookingEntity(
        id = id,
        orderId = orderId,
        subServiceId = subServiceId,
        subServiceName = subServiceName,
        subServiceImage = subServiceImage,
        providerId = providerId,
        providerName = providerName,
        providerImage = providerImage,
        providerPhone = providerPhone,
        providerRating = providerRating,
        providerFeeCents = providerFee,
        servicePriceCents = servicePriceCents,
        totalAmountCents = totalAmountCents,
        bookingDate = bookingDate,
        scheduledDate = scheduledDate,
        address = address,
        status = status.name,
        paymentStatus = paymentStatus.name
    )
}

