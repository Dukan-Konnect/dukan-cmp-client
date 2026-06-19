package org.example.project.home.domain.usecase

import kotlin.random.Random
import kotlin.math.abs
import org.example.project.payment.domain.model.CreateOrderRequest
import org.example.project.payment.domain.model.PaymentOrder
import org.example.project.home.domain.repository.PaymentRepository


class CreatePaymentOrderUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(amountInCents: Long): Result<PaymentOrder> {
        val numericSuffix = abs(Random.nextLong())
        val receipt = "rcpt_${numericSuffix}_${randomAlphaNum()}"

        val request = CreateOrderRequest(
            amount = amountInCents,
            currency = "INR",
            receipt = receipt
        )

        return paymentRepository.createOrder(request)
    }

    private fun randomAlphaNum(): String {
        val length = 8
        val pool = (('a'..'z') + ('A'..'Z') + ('0'..'9')).toList()
        return (1..length)
            .map { pool[Random.nextInt(pool.size)] }
            .joinToString("")
    }
}
