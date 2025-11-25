package org.example.project.home.domain.usecase

import org.example.project.home.domain.model.CreateOrderRequest
import org.example.project.home.domain.model.PaymentOrder
import org.example.project.home.domain.repository.PaymentRepository
import java.util.UUID
import kotlin.time.Clock

class CreatePaymentOrderUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(amountInCents: Long): Result<PaymentOrder> {
        // Generate a unique receipt ID
        val receipt = "rcpt_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"

        val request = CreateOrderRequest(
            amount = amountInCents,
            currency = "INR",
            receipt = receipt
        )

        return paymentRepository.createOrder(request)
    }
}

