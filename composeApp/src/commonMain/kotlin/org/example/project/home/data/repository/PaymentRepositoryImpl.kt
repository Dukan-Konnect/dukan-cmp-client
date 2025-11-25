package org.example.project.home.data.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.example.project.core.log
import org.example.project.home.data.model.RazorpayOrderRequest
import org.example.project.home.data.model.RazorpayOrderResponse
import org.example.project.home.domain.model.CreateOrderRequest
import org.example.project.home.domain.model.PaymentOrder
import org.example.project.home.domain.repository.PaymentRepository

class PaymentRepositoryImpl(
    private val httpClient: HttpClient,
    private val razorpayKeyId: String,
    private val razorpayKeySecret: String
) : PaymentRepository {

    private companion object {
        const val BASE_URL = "https://api.razorpay.com/v1"
        const val DEFAULT_CURRENCY = "INR"
    }

    override suspend fun createOrder(request: CreateOrderRequest): Result<PaymentOrder> {
        return withContext(Dispatchers.Default) {
            try {
                // Normalize inputs
                val safeCurrency = request.currency.trim().uppercase().ifEmpty { DEFAULT_CURRENCY }
                if (request.amount <= 0) {
                    return@withContext Result.failure(IllegalArgumentException("Amount must be > 0 (in paise)"))
                }

                val razorpayRequest = RazorpayOrderRequest(
                    amount = request.amount,
                    currency = safeCurrency,
                    receipt = request.receipt
                )

                // Helpful debug log
                log("paymentviewmodel", "Creating Razorpay order: amount=${razorpayRequest.amount}, currency=${razorpayRequest.currency}, receipt=${razorpayRequest.receipt}")

                val jsonSerializer = Json { // replicate ContentNegotiation settings
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
                val jsonPayload = jsonSerializer.encodeToString(razorpayRequest)
                log("paymentviewmodel", "Outgoing Razorpay order JSON payload: $jsonPayload")

                val response: HttpResponse = httpClient.post("$BASE_URL/orders") {
                    contentType(ContentType.Application.Json)

                    // Basic authentication with Razorpay credentials
                    val credentials = "$razorpayKeyId:$razorpayKeySecret"
                    val encodedCredentials = credentials.encodeBase64()
                    header(HttpHeaders.Authorization, "Basic $encodedCredentials")
                    log("paymentviewmodel", "Authorization header (Basic base64 id:secret) applied. Encoded length=${encodedCredentials.length}")

                    setBody(razorpayRequest)
                }

                if (response.status.isSuccess()) {
                    val razorpayResponse: RazorpayOrderResponse = response.body()
                    val paymentOrder = PaymentOrder(
                        orderId = razorpayResponse.id,
                        amount = razorpayResponse.amount,
                        currency = razorpayResponse.currency,
                        receipt = razorpayResponse.receipt,
                        status = razorpayResponse.status
                    )
                    Result.success(paymentOrder)
                } else {
                    val errorBody = response.bodyAsText()
                    log("paymentviewmodel", "Failed to create order: ${response.status.value} - $errorBody")
                    Result.failure(Exception("Failed to create order: ${response.status.value} - $errorBody"))

                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
