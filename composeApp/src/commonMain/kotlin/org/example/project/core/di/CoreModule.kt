package org.example.project.core.di

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

import org.koin.dsl.module

//val coreModule = module {
//
//    // HttpClient for API calls
//    single {
//        HttpClient {
//            install(ContentNegotiation) {
//                json(Json {
//                    prettyPrint = true
//                    isLenient = true
//                    ignoreUnknownKeys = true
//                })
//            }
//
//
//            install(HttpTimeout) {
//                requestTimeoutMillis = 30000
//                connectTimeoutMillis = 30000
//                socketTimeoutMillis = 30000
//            }
//
//            defaultRequest {
//                url("https://api.razorpay.com/v1/")
//            }
//        }
//    }
//}

