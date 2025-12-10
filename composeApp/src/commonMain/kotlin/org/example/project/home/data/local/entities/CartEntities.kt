package org.example.project.home.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: String, // subservice id

    @ColumnInfo(name = "name")
    val name: String, // subservice name

    @ColumnInfo(name = "price_cents")
    val priceCents: Long, // subservice base price

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    // Service provider fields
    @ColumnInfo(name = "provider_id")
    val providerId: String,

    @ColumnInfo(name = "provider_name")
    val providerName: String,

    @ColumnInfo(name = "provider_image_url")
    val providerImageUrl: String,

    @ColumnInfo(name = "provider_phone_number")
    val providerPhoneNumber: String,

    @ColumnInfo(name = "provider_rating")
    val providerRating: Double,

    @ColumnInfo(name = "provider_fee_cents")
    val providerFeeCents: Long,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)

@Entity(tableName = "cart_summary")
data class CartSummaryEntity(
    @PrimaryKey
    val id: Int = 1, // Single row table

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "address")
    val address: String? = null,

    @ColumnInfo(name = "time_slot")
    val timeSlot: String? = null,

    @ColumnInfo(name = "tax_percent")
    val taxPercent: Double = 5.0,

    @ColumnInfo(name = "delivery_charges_cents")
    val deliveryChargesCents: Long = 0L,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
