package org.example.project.home.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class CategoryItem(
    val id: String, // uuid
    val label: String,
    val image: String
)

@Immutable
data class SubService(
    val id: String, // uuid
    val title: String,
    val rating: Double,
    val reviewCount: Int,
    val durationMin: Int?,
    val price: Int, // in major currency (e.g., INR)
    val currency: String = "INR",
    val image: String
) {
    val ratingText: String
        get() {
            val duration = durationMin?.let { "$it min" } ?: ""
            return if (duration.isNotEmpty()) "$rating ($reviewCount) | $duration" else "$rating ($reviewCount)"
        }
}

@Immutable
data class ServiceSection(
    val id: String, // section id (use category id)
    val title: String,
    val items: List<SubService>
)

@Immutable
data class ServiceDetails(
    val id: Long,
    val title: String,
    val bannerTitle: String,
    val bannerImage: String,
    val rating: Double,
    val reviewCount: Int,
    val bookingsText: String,
    val categories: List<CategoryItem>,
    val sections: List<ServiceSection>
) {
    val ratingText: String
        get() = "$rating ($reviewCount)"
}

@Immutable
data class ServiceProvider(
    val id: String,
    val name: String,
    val imageUrl: String,
    val phoneNumber: String,
    val rating: Double,
    val fee: Int, // actual fee amount in rupees (e.g., 500 for ₹500)
    val subserviceId: String
)

