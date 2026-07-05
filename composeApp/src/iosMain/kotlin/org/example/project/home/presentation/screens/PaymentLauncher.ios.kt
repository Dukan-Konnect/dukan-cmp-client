package org.example.project.home.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSUUID

@Composable
actual fun LaunchPaymentActivity(
    orderId: String,
    amount: Long,
    phoneNumber: String,
    onResult: (Boolean) -> Unit
) {
    val requestId = remember(orderId, amount, phoneNumber) {
        NSUUID().UUIDString
    }

    var completed by remember(requestId) {
        mutableStateOf(false)
    }

    DisposableEffect(requestId) {
        val center = NSNotificationCenter.defaultCenter
        val observer = center.addObserverForName(
            name = PAYMENT_RESULT_NOTIFICATION,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { notification ->
            val userInfo = notification?.userInfo ?: return@addObserverForName
            val notificationRequestId = userInfo[PAYMENT_REQUEST_ID_KEY] as? String ?: return@addObserverForName
            if (notificationRequestId != requestId || completed) return@addObserverForName

            completed = true
            val success = (userInfo[PAYMENT_SUCCESS_KEY] as? Boolean) ?: false
            println("[SummaryScreen] success = $success")
            onResult(success)
        }

        val userInfo: Map<Any?, Any?> = mapOf(
            PAYMENT_REQUEST_ID_KEY to requestId,
            PAYMENT_ORDER_ID_KEY to orderId,
            PAYMENT_AMOUNT_KEY to amount,
            PAYMENT_PHONE_KEY to phoneNumber
        )
        center.postNotificationName(PAYMENT_REQUEST_NOTIFICATION, null, userInfo)

        onDispose {
            center.removeObserver(observer)
        }
    }
}

private const val PAYMENT_REQUEST_NOTIFICATION = "DukaanKonnectPresentRazorpay"
private const val PAYMENT_RESULT_NOTIFICATION = "DukaanKonnectRazorpayResult"
private const val PAYMENT_REQUEST_ID_KEY = "requestId"
private const val PAYMENT_ORDER_ID_KEY = "orderId"
private const val PAYMENT_AMOUNT_KEY = "amount"
private const val PAYMENT_PHONE_KEY = "phoneNumber"
private const val PAYMENT_SUCCESS_KEY = "success"
