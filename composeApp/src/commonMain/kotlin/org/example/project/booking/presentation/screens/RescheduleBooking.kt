package org.example.project.booking.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

data class ServiceItem(
    val name: String,
    val duration: String,
    val info: String
)

data class DateOption(
    val dayName: String,
    val dayNumber: String
)

data class TimeSlot(
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescheduleBookingScreen(
    services: List<ServiceItem> = listOf(
        ServiceItem("Diamond Facial", "2 hrs", "Includes dummy info"),
        ServiceItem("Cleanup", "30 mins", "Includes dummy info")
    ),
    onBackClick: () -> Unit = {},
    onConfirmSlot: (date: String, time: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf<Int?>(null) }
    var selectedTime by remember { mutableStateOf<Int?>(null) }

    val dateOptions = listOf(
        DateOption("Tue", "22"),
        DateOption("Wed", "23"),
        DateOption("Thu", "24")
    )

    val timeSlots = listOf(
        TimeSlot("09:30 AM"),
        TimeSlot("10:00 AM"),
        TimeSlot("10:30 AM")
    )

    val isSlotSelected = selectedDate != null && selectedTime != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reschedule Booking") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Service Items
            services.forEach { service ->
                ServiceInfoCard(
                    serviceName = service.name,
                    duration = service.duration,
                    info = service.info
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Select new date and time section
            Text(
                text = "Select new date and time",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Your service will take approx. 45 mins",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Date Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                dateOptions.forEachIndexed { index, date ->
                    DateChip(
                        dayName = date.dayName,
                        dayNumber = date.dayNumber,
                        isSelected = selectedDate == index,
                        onClick = { selectedDate = index },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                timeSlots.forEachIndexed { index, timeSlot ->
                    TimeChip(
                        time = timeSlot.time,
                        isSelected = selectedTime == index,
                        onClick = { selectedTime = index },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Confirm Button
            Button(
                onClick = {
                    if (selectedDate != null && selectedTime != null) {
                        val date = "${dateOptions[selectedDate!!].dayName} ${dateOptions[selectedDate!!].dayNumber}"
                        val time = timeSlots[selectedTime!!].time
                        onConfirmSlot(date, time)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = isSlotSelected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Confirm new slot",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSlotSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ServiceInfoCard(
    serviceName: String,
    duration: String,
    info: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Placeholder image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Image", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = serviceName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "• $duration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• $info",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DateChip(
    dayName: String,
    dayNumber: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = dayNumber,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun TimeChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview
@Composable
fun PreviewRescheduleBookingScreen() {
    MaterialTheme {
        Surface {
            RescheduleBookingScreen()
        }
    }
}