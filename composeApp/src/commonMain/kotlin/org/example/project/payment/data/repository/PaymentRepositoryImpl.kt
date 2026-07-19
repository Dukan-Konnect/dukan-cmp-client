package org.example.project.payment.data.repository

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.example.project.core.log
import org.example.project.core.utils.mapHttpStatusToUserMessage
import org.example.project.core.utils.SnackbarMessage
import org.example.project.payment.domain.model.CreateOrderRequest
import org.example.project.payment.domain.model.PaymentOrder
import org.example.project.payment.domain.repository.PaymentRepository
import org.example.project.payment.data.model.RazorpayOrderRequest
import org.example.project.payment.data.model.RazorpayOrderResponse

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
                val safeCurrency = request.currency.trim().uppercase().ifEmpty { DEFAULT_CURRENCY }
                if (request.amount <= 0) {
                    return@withContext Result.failure(IllegalArgumentException("Amount must be > 0 (in paise)"))
                }

                val razorpayRequest = RazorpayOrderRequest(
                    amount = request.amount,
                    currency = safeCurrency,
                    receipt = request.receipt
                )

                log(
                    "paymentviewmodel",
                    "Creating Razorpay order: amount=${razorpayRequest.amount}, currency=${razorpayRequest.currency}, receipt=${razorpayRequest.receipt}"
                )

                val jsonSerializer = Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
                val jsonPayload = jsonSerializer.encodeToString(razorpayRequest)
                log("paymentviewmodel", "Outgoing Razorpay order JSON payload: $jsonPayload")

                val response: HttpResponse = httpClient.post("$BASE_URL/orders") {
                    contentType(ContentType.Application.Json)

                    val credentials = "$razorpayKeyId:$razorpayKeySecret"
                    val encodedCredentials = credentials.encodeBase64()
                    headers {
                        remove(HttpHeaders.Authorization)
                        append(HttpHeaders.Authorization, "Basic $encodedCredentials")
                    }
                    log(
                        "paymentviewmodel",
                        "Authorization header (Basic base64 id:secret) applied. Encoded length=${encodedCredentials.length}"
                    )

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
                    val userMessage = mapHttpStatusToUserMessage(response.status.value)
                    log(
                        "paymentviewmodel",
                        "Failed to create order: ${response.status.value} - $errorBody"
                    )
                    Result.failure(Exception(userMessage))

                }
            } catch (e: ClientRequestException) {
                Result.failure(Exception(mapHttpStatusToUserMessage(e.response.status.value), e))
            } catch (e: ServerResponseException) {
                Result.failure(Exception(SnackbarMessage.SERVER_ERROR, e))
            } catch (e: IOException) {
                Result.failure(Exception(SnackbarMessage.NETWORK_ERROR, e))
            } catch (e: Exception) {
                Result.failure(Exception(SnackbarMessage.GENERIC_ERROR, e))
            }
        }
    }
}
