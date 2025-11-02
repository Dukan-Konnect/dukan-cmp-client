package org.example.project.booking.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    isUpcoming: Boolean,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isUpcoming) "Upcoming Booking" else "Previous Booking") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        // Simple back arrow using text
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Booking Details",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    DetailRow(label = "Date", value = "Oct 25, 2025")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow(label = "Time", value = "2:00 PM - 3:00 PM")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow(label = "Location", value = "Conference Room A")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow(label = "Duration", value = "1 hour")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow(
                        label = "Status",
                        value = if (isUpcoming) "Confirmed" else "Completed"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isUpcoming) {
                Button(
                    onClick = { /* Handle cancel */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancel Booking")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun PreviewBookingDetailScreenUpcoming() {
    MaterialTheme {
        Surface {
            BookingDetailScreen(isUpcoming = true)
        }
    }
}

@Preview
@Composable
fun PreviewBookingDetailScreenPrevious() {
    MaterialTheme {
        Surface {
            BookingDetailScreen(isUpcoming = false)
        }
    }
}