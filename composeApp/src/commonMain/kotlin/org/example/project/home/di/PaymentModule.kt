package org.example.project.home.di

import io.ktor.client.*
import org.example.project.home.data.repository.PaymentRepositoryImpl
import org.example.project.home.domain.repository.PaymentRepository
import org.example.project.home.domain.usecase.CreatePaymentOrderUseCase
import org.koin.dsl.module

val paymentModule = module {

    // Repository
    single<PaymentRepository> {
        PaymentRepositoryImpl(
            httpClient = get<HttpClient>(),
            razorpayKeyId = getProperty("razorpay.key.id"),
            razorpayKeySecret = getProperty("razorpay.key.secret")
        )
    }

    // Use Cases
    single { CreatePaymentOrderUseCase(get()) }
}

