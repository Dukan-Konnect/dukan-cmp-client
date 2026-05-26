package org.example.project.core.network.dto.booking

import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequestDto(
    val subServiceId: String,
    val providerId: String,
    val scheduledDate: String,
    val address: String
)

