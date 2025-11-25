package org.example.project.home.presentation.screens

import androidx.compose.runtime.Composable

@Composable
expect fun LaunchPaymentActivity(
    orderId: String,
    amount: Long,
    phoneNumber: String,
    onResult: (Boolean) -> Unit
)

