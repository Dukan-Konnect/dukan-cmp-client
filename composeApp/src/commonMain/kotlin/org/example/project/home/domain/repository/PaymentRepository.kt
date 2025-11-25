package org.example.project.home.domain.repository

import org.example.project.home.domain.model.CreateOrderRequest
import org.example.project.home.domain.model.PaymentOrder

interface PaymentRepository {
    suspend fun createOrder(request: CreateOrderRequest): Result<PaymentOrder>
}

