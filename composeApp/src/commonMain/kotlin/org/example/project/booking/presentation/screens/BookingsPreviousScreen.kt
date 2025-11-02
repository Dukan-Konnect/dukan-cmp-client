package org.example.project.booking.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BookingsPreviousScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Previous Bookings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // TODO: Replace with actual booking data
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Placeholder for booking items
            items(5) { index ->
                BookingCard(
                    title = "Booking ${index + 1}",
                    date = "Oct ${10 + index}, 2025",
                    time = "10:00 AM",
                    location = "Meeting Room ${index + 1}",
                    isUpcoming = false,
                    onCardClick = { /* Navigate to detail */ }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBookingsPreviousScreen() {
    MaterialTheme {
        Surface {
            BookingsPreviousScreen()
        }
    }
}