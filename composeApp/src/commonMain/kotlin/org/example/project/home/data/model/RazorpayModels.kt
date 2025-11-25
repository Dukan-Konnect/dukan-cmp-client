package org.example.project.home.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RazorpayOrderRequest(
    @SerialName("amount")
    val amount: Long,
    @SerialName("currency")
    val currency: String,
    @SerialName("receipt")
    val receipt: String
)

@Serializable
data class RazorpayOrderResponse(
    @SerialName("id")
    val id: String,
    @SerialName("entity")
    val entity: String,
    @SerialName("amount")
    val amount: Long,
    @SerialName("amount_paid")
    val amountPaid: Long,
    @SerialName("amount_due")
    val amountDue: Long,
    @SerialName("currency")
    val currency: String,
    @SerialName("receipt")
    val receipt: String,
    @SerialName("status")
    val status: String,
    @SerialName("attempts")
    val attempts: Int,
    @SerialName("created_at")
    val createdAt: Long
)

