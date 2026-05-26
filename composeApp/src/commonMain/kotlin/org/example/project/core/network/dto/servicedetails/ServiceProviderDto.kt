package org.example.project.core.network.dto.servicedetails

import kotlinx.serialization.Serializable

@Serializable
data class ServiceProviderDto(
    val id: String,
    val subserviceId: String,
    val name: String,
    val imageUrl: String = "",
    val phoneNumber: String = "",
    val rating: Double = 0.0,
    val fee: Int = 0
)

