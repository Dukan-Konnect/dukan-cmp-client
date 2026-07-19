package org.example.project.payment.domain.repository

import org.example.project.payment.domain.model.CreateOrderRequest
import org.example.project.payment.domain.model.PaymentOrder

interface PaymentRepository {
    suspend fun createOrder(request: CreateOrderRequest): Result<PaymentOrder>
}