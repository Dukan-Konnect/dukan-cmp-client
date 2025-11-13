package org.example.project.home.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "price_cents")
    val priceCents: Long,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)

@Entity(tableName = "cart_summary")
data class CartSummaryEntity(
    @PrimaryKey
    val id: Int = 1, // Single row table

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "address")
    val address: String? = null,

    @ColumnInfo(name = "time_slot")
    val timeSlot: String? = null,

    @ColumnInfo(name = "tax_percent")
    val taxPercent: Double = 0.0,

    @ColumnInfo(name = "delivery_charges_cents")
    val deliveryChargesCents: Long = 0L,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
