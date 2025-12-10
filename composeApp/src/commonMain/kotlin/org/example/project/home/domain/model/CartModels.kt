package org.example.project.home.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val productId: String, // subservice id
    val name: String, // subservice name
    val priceCents: Long, // subservice base price in cents
    val imageUrl: String? = null,
    // Service provider info
    val providerId: String,
    val providerName: String,
    val providerImageUrl: String,
    val providerPhoneNumber: String,
    val providerRating: Double,
    val providerFeeCents: Long // provider fee in cents
) {
    val totalPriceCents: Long
        get() = providerFeeCents // Only charge the provider fee
}

@Serializable
data class CartSummary(
    val name: String? = null,
    val phoneNumber: String,
    val address: String? = null,
    val timeSlot: String? = null,
    val taxPercent: Double = 5.0,
    val deliveryChargesCents: Long = 0L
) {
    companion object {
        fun calculateTotals(items: List<CartItem>, summary: CartSummary): CartTotals {
            val itemTotalCents = items.sumOf { it.totalPriceCents }
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
