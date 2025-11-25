package org.example.project.home.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val productId: String,
    val name: String,
    val priceCents: Long, // Price in cents to avoid decimal issues
    val quantity: Int,
    val imageUrl: String? = null
)

@Serializable
data class CartSummary(
    val name: String? = null,
    val phoneNumber: String,
    val address: String? = null,
    val timeSlot: String? = null,
    val taxPercent: Double = 0.0,
    val deliveryChargesCents: Long = 0L
) {
    companion object {
        fun calculateTotals(items: List<CartItem>, summary: CartSummary): CartTotals {
            val itemTotalCents = items.sumOf { it.priceCents * it.quantity }
            val taxesCents = (itemTotalCents * summary.taxPercent / 100.0).toLong()
            val totalCents = itemTotalCents + taxesCents + summary.deliveryChargesCents

            return CartTotals(
                itemTotalCents = itemTotalCents,
                taxesCents = taxesCents,
                deliveryChargesCents = summary.deliveryChargesCents,
                totalCents = totalCents,
                amountToPayCents = totalCents
            )
        }
    }
}

@Serializable
data class CartTotals(
    val itemTotalCents: Long,
    val taxesCents: Long,
    val deliveryChargesCents: Long,
    val totalCents: Long,
    val amountToPayCents: Long
)

@Serializable
data class CartData(
    val items: List<CartItem> = emptyList(),
    val summary: CartSummary? = null,
    val totals: CartTotals? = null
)
