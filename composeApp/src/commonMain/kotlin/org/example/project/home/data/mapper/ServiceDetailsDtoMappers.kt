package org.example.project.home.data.mapper

import org.example.project.core.network.dto.servicedetails.ServiceDetailsResponseDto
import org.example.project.core.network.dto.servicedetails.ServiceProviderDto
import org.example.project.home.domain.model.CategoryItem
import org.example.project.home.domain.model.ServiceDetails
import org.example.project.home.domain.model.ServiceProvider
import org.example.project.home.domain.model.ServiceSection
import org.example.project.home.domain.model.SubService

fun ServiceDetailsResponseDto.toDomain(): ServiceDetails {
    return ServiceDetails(
        id = id,
        title = title,
        bannerTitle = bannerTitle,
        bannerImage = bannerImage,
        rating = rating,
        reviewCount = reviewCount,
        bookingsText = bookingsText,
        categories = categories.map {
            CategoryItem(
                id = it.id,
                label = it.label,
                image = it.image
            )
        },
        sections = sections.map { section ->
            ServiceSection(
                id = section.id,
                title = section.title,
                items = section.items.map { item ->
                    SubService(
                        id = item.id,
                        title = item.title,
                        rating = item.rating,
                        reviewCount = item.reviewCount,
                        durationMin = item.durationMin,
                        price = item.price,
                        currency = item.currency,
                        image = item.image
                    )
                }
            )
        }
    )
}

fun ServiceProviderDto.toDomain(): ServiceProvider {
    return ServiceProvider(
        id = id,
        name = name,
        imageUrl = imageUrl,
        phoneNumber = phoneNumber,
        rating = rating,
        fee = fee,
        subserviceId = subserviceId
    )
}

