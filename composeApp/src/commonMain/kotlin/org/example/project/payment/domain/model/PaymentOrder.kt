package org.example.project.payment.domain.model

data class PaymentOrder(
    val orderId: String,
    val amount: Long, // in paise/cents
    val currency: String = "INR",
    val receipt: String,
    val status: String
)

data class CreateOrderRequest(
    val amount: Long, // in paise/cents
    val currency: String = "INR",
    val receipt: String
)

