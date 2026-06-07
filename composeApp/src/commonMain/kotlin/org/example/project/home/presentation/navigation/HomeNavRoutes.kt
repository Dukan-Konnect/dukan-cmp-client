package org.example.project.home.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data class ServiceDetailRoute(val serviceId: Long)

@Serializable
data class SummaryRoute(
    val serviceId: Long,
    val serviceTitle: String,
    val subServiceId: String,
    val subServiceTitle: String,
    val subServiceImage: String,
    val subServicePrice: Int,
    val providerId: String,
    val providerName: String,
    val providerImageUrl: String,
    val providerPhoneNumber: String,
    val providerRating: Double,
    val providerFee: Int
)

@Serializable
object EditProfileRoute
