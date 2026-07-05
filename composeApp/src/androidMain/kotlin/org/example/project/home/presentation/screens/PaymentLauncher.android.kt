package org.example.project.home.presentation.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import org.example.project.PaymentActivity

@Composable
actual fun LaunchPaymentActivity(
    orderId: String,
    amount: Long,
    phoneNumber: String,
    onResult: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> onResult(true)
            Activity.RESULT_CANCELED -> onResult(false)
            else -> onResult(false)
        }
    }

    LaunchedEffect(orderId, amount) {
        val intent = Intent(context, PaymentActivity::class.java).apply {
            putExtra(PaymentActivity.EXTRA_ORDER_ID, orderId)
            putExtra(PaymentActivity.EXTRA_AMOUNT, amount)
            putExtra(PaymentActivity.EXTRA_PHONE_NUMBER, phoneNumber)
        }
        launcher.launch(intent)
    }
}
