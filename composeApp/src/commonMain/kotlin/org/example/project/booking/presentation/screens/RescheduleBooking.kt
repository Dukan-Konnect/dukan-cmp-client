package org.example.project.booking.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.ic_arrow_back
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.example.project.booking.presentation.viewmodels.BookingsViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescheduleBookingScreen(
    bookingId: String,
    viewModel: BookingsViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onConfirmSlot: (date: String, time: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val booking = state.bookings.find { it.id == bookingId }

    if (booking == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // --- Date and Time Logic ---
    val timeSlots = remember {
        listOf(
            LocalTime(8, 0), LocalTime(10, 0), LocalTime(12, 0),
            LocalTime(14, 0), LocalTime(16, 0), LocalTime(18, 0)
        )
    }

    val currentMoment = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    val today = currentMoment.date

    val validDates = remember {
        val nowInstant = Clock.System.now()
        val twoHoursFromNow = nowInstant.plus(2.hours)

        // Check if all timeslots for 'today' are in the past (+ 2 hours margin)
        val allTodaySlotsInvalid = timeSlots.all { time ->
            val slotDateTime = LocalDateTime(
                today.year,
                today.monthNumber,
                today.dayOfMonth,
                time.hour,
                time.minute
            )
            slotDateTime.toInstant(TimeZone.currentSystemDefault()) < twoHoursFromNow
        }

        // If today is burned out, start from tomorrow
        val startOffset = if (allTodaySlotsInvalid) 1 else 0
        (startOffset..startOffset + 2).map { today.plus(it, DateTimeUnit.DAY) }
    }

    val dateFormatter = remember {
        LocalDate.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            dayOfMonth()
        }
    }

    val timeFormatter = remember {
        LocalTime.Format {
            amPmHour()
            char(':')
            minute()
            char(' ')
            amPmMarker("AM", "PM")
        }
    }

    var selectedDate by remember { mutableStateOf(validDates.first()) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reschedule Booking") },
                windowInsets = WindowInsets(0.dp),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = Color.Black
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
            // Real Service Item
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = booking.subServiceImage,
                        contentDescription = booking.subServiceName,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = booking.subServiceName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "• 1 hour",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Provider: ${booking.providerName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date and Time Section
            Text(
                text = "Select new date and time",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Your service will take approx. 1 hour",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Date Selection
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(validDates) { date ->
                    val isSelected = date == selectedDate
                    val dayOfWeek = date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                    val dayOfMonth = date.dayOfMonth.toString()

                    Surface(
                        modifier = Modifier
                            .size(width = 64.dp, height = 72.dp)
                            .clickable {
                                selectedDate = date
                                selectedTime = null
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFECE7FF) else Color.Transparent,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = dayOfWeek,
                                fontSize = 14.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dayOfMonth,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Time Selection
            val chunkedTimes = timeSlots.chunked(3)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                chunkedTimes.forEach { rowTimes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowTimes.forEach { time ->
                            val slotDateTime = LocalDateTime(
                                year = selectedDate.year,
                                monthNumber = selectedDate.monthNumber,
                                dayOfMonth = selectedDate.dayOfMonth,
                                hour = time.hour,
                                minute = time.minute
                            )
                            val slotInstant = slotDateTime.toInstant(TimeZone.currentSystemDefault())

                            val isPastTime = slotInstant < Clock.System.now().plus(1.hours)
                            val isSelected = time == selectedTime

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable(enabled = !isPastTime) {
                                        selectedTime = time
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFECE7FF) else if (isPastTime) Color(0xFFF9F9F9) else Color.Transparent,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else if (isPastTime) Color.Transparent else Color(0xFFE0E0E0)
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = time.format(timeFormatter),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isPastTime) Color.LightGray else if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
                                    )
                                }
                            }
                        }

                        // Fill empty spaces if a row has less than 3 items
                        repeat(3 - rowTimes.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Confirm Button
            val isSlotSelected = selectedTime != null
            Button(
                onClick = {
                    if (isSlotSelected) {
                        val dateFormatted = selectedDate.format(dateFormatter)
                        val timeFormatted = selectedTime!!.format(timeFormatter)
                        onConfirmSlot(dateFormatted, timeFormatted)
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