package org.example.project.booking.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "order_id")
    val orderId: String,

    @ColumnInfo(name = "sub_service_id")
    val subServiceId: String,

    @ColumnInfo(name = "sub_service_name")
    val subServiceName: String,

    @ColumnInfo(name = "sub_service_image")
    val subServiceImage: String?,

    @ColumnInfo(name = "provider_id")
    val providerId: String,

    @ColumnInfo(name = "provider_name")
    val providerName: String,

    @ColumnInfo(name = "provider_image")
    val providerImage: String,

    @ColumnInfo(name = "provider_phone")
    val providerPhone: String,

    @ColumnInfo(name = "provider_rating")
    val providerRating: Double,

    @ColumnInfo(name = "provider_fee_cents")
    val providerFeeCents: Long,

    @ColumnInfo(name = "service_price_cents")
    val servicePriceCents: Long,

    @ColumnInfo(name = "total_amount_cents")
    val totalAmountCents: Long,

    @ColumnInfo(name = "booking_date")
    val bookingDate: Long,

    @ColumnInfo(name = "scheduled_date")
    val scheduledDate: String?,

    @ColumnInfo(name = "address")
    val address: String?,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "payment_status")
    val paymentStatus: String
)