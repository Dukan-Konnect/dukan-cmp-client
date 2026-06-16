package org.example.project.booking.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.ic_arrow_back
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.example.project.booking.presentation.viewmodels.BookingsViewModel
import org.example.project.booking.util.formatScheduledDateString
import org.example.project.core.model.booking.BookingStatus
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingId: String,
    viewModel: BookingsViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onRescheduleClick: (String) -> Unit = {},
    onCancelClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    val booking = state.bookings.find { it.id == bookingId }

    if (booking == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isRescheduleDisabled = remember(booking.scheduledDate) {
        checkIfRescheduleDisabled(booking.scheduledDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details") },
                windowInsets = WindowInsets(0.dp),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = Color.Black
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
                text = booking.subServiceName,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    DetailRow(
                        label = "Scheduled For",
                        value = booking.scheduledDate?.let { formatScheduledDateString(it) } ?: "Not scheduled"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow(
                        label = "Location",
                        value = booking.address ?: "No address provided"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow(
                        label = "Duration",
                        value = "1 hour"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow(
                        label = "Status",
                        value = booking.status.name.lowercase().replaceFirstChar { it.uppercase() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (booking.status == BookingStatus.PENDING || booking.status == BookingStatus.CONFIRMED) {

                Button(
                    onClick = { onRescheduleClick(booking.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    enabled = !isRescheduleDisabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text("Reschedule Booking")
                }

                Button(
                    onClick = { onCancelClick(booking.id) },
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

private fun checkIfRescheduleDisabled(scheduledDateStr: String?): Boolean {
    if (scheduledDateStr.isNullOrBlank()) return false

    return try {
        val scheduledInstant = try {
            kotlinx.datetime.Instant.parse(scheduledDateStr)
        } catch (_: Exception) {
            val dateTime = LocalDateTime.parse(scheduledDateStr)
            dateTime.toInstant(TimeZone.currentSystemDefault())
        }

        val now = Clock.System.now()
        val scheduledDateTime = scheduledInstant.toLocalDateTime(TimeZone.currentSystemDefault())
        val nowDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

        val isToday = scheduledDateTime.date == nowDateTime.date
        val minutesUntil = (scheduledInstant - now).inWholeMinutes

        isToday && minutesUntil < 45
    } catch (_: Exception) {
        false
    }
}