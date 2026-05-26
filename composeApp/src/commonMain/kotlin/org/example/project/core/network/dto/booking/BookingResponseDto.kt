package org.example.project.core.network.dto.booking

import kotlinx.serialization.Serializable

@Serializable
data class BookingResponseDto(
    val id: String,
    val orderId: String,
    val subServiceId: String,
    val subServiceName: String,
    val subServiceImage: String? = null,
    val providerId: String,
    val providerName: String,
    val providerImage: String,
    val providerPhone: String,
    val providerRating: Double? = null,
    val providerFeeCents: Long? = null,
    val servicePriceCents: Long? = null,
    val totalAmountCents: Long? = null,
    val bookingDate: Long? = null,
    val scheduledDate: String? = null,
    val address: String? = null,
    val status: String? = null,
    val paymentStatus: String? = null
)

