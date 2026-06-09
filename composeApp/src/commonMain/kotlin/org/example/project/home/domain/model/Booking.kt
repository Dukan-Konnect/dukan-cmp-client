package org.example.project.home.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Booking(
    val id: String,
    val orderId: String,
    val subServiceId: String,
    val subServiceName: String,
    val subServiceImage: String?,
    val providerId: String,
    val providerName: String,
    val providerImage: String,
    val providerPhone: String,
    val providerRating: Double,
    val providerFee: Long, // in cents
    val servicePriceCents: Long,
    val totalAmountCents: Long,
    val bookingDate: Long, // timestamp
    val scheduledDate: String?, // scheduled date/time slot
    val address: String?,
    val status: BookingStatus,
    val paymentStatus: PaymentStatus
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
}

enum class PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED
}

