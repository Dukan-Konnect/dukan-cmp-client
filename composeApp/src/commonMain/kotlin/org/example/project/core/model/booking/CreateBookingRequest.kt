package org.example.project.core.model.booking

data class CreateBookingRequest(
    val subServiceId: String,
    val providerId: String,
    val scheduledDate: String,
    val address: String
)