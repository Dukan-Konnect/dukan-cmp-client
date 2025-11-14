package org.example.project.home.data.local.mappers

import org.example.project.home.data.local.entities.CartItemEntity
import org.example.project.home.data.local.entities.CartSummaryEntity
import org.example.project.home.data.local.util.getCurrentTimeMillis
import org.example.project.home.domain.model.CartItem
import org.example.project.home.domain.model.CartSummary

// CartItem mappers
fun CartItem.toEntity(): CartItemEntity {
    val now = getCurrentTimeMillis()
    return CartItemEntity(
        productId = productId,
        name = name,
        priceCents = priceCents,
        quantity = quantity,
        imageUrl = imageUrl,
        createdAt = now,
        updatedAt = now
    )
}

fun CartItemEntity.toDomainModel(): CartItem = CartItem(
    productId = productId,
    name = name,
    priceCents = priceCents,
    quantity = quantity,
    imageUrl = imageUrl
)

fun List<CartItemEntity>.toDomainModels(): List<CartItem> = map { it.toDomainModel() }

// CartSummary mappers
fun CartSummary.toEntity(): CartSummaryEntity {
    val now = getCurrentTimeMillis()
    return CartSummaryEntity(
        phoneNumber = phoneNumber,
        address = address,
        timeSlot = timeSlot,
        taxPercent = taxPercent,
        deliveryChargesCents = deliveryChargesCents,
        createdAt = now,
        updatedAt = now
    )
}

fun CartSummaryEntity.toDomainModel(): CartSummary = CartSummary(
    phoneNumber = phoneNumber,
    address = address,
    timeSlot = timeSlot,
    taxPercent = taxPercent,
    deliveryChargesCents = deliveryChargesCents
)
