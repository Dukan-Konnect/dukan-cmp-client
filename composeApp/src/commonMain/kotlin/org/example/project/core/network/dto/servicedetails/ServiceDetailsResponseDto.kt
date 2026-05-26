package org.example.project.core.network.dto.servicedetails

import kotlinx.serialization.Serializable

@Serializable
data class ServiceDetailsResponseDto(
    val id: Long,
    val title: String,
    val bannerTitle: String,
    val bannerImage: String,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val bookingsText: String = "Same day bookings available",
    val categories: List<CategoryItemDto> = emptyList(),
    val sections: List<ServiceSectionDto> = emptyList()
)

@Serializable
data class CategoryItemDto(
    val id: String,
    val label: String,
    val image: String = ""
)

@Serializable
data class ServiceSectionDto(
    val id: String,
    val title: String,
    val items: List<SubServiceDto> = emptyList()
)

@Serializable
data class SubServiceDto(
    val id: String,
    val title: String,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val durationMin: Int? = null,
    val price: Int = 0,
    val currency: String = "INR",
    val image: String = ""
)

