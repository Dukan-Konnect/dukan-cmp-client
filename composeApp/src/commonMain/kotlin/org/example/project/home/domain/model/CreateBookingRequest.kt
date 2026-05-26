package org.example.project.home.domain.model

data class CreateBookingRequest(
    val subServiceId: String,
    val providerId: String,
    val scheduledDate: String,
    val address: String
)

