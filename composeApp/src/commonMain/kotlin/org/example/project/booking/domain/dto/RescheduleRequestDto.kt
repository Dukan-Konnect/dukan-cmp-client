package org.example.project.booking.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class RescheduleRequestDto(
    val newScheduledDate: String
)