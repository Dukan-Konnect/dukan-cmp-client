package org.example.project.home.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class CategoryItem(
    val id: Int,
    val label: String,
    val image: String
)

@Immutable
data class SubService(
    val id: Int,
    val title: String,
    val rating: Double,
    val reviewCount: String,
    val duration: String,
    val price: Int,
    val image: String
) {
    val ratingText: String
        get() = "$rating ($reviewCount) | $duration"
}

@Immutable
data class ServiceSection(
    val id: Int,
    val title: String,
    val items: List<SubService>
)

@Immutable
data class ServiceDetails(
    val id: Int,
    val title: String,
    val bannerTitle: String,
    val bannerImage: String,
    val rating: Double,
    val reviewCount: String,
    val bookingsText: String,
    val categories: List<CategoryItem>,
    val sections: List<ServiceSection>
) {
    val ratingText: String
        get() = "$rating ($reviewCount)"
}
