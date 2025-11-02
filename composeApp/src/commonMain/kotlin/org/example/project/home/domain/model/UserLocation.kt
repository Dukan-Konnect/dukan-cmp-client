package org.example.project.home.domain.model

data class UserLocation(
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

