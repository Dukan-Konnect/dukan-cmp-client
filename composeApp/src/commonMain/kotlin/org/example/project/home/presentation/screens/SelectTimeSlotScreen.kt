package org.example.project.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

data class DateOption(
    val day: String,
    val date: Int,
    val month: String = "Oct"
)

data class TimeSlot(
    val time: String,
    val isHighDemand: Boolean = false,
    val isAvailable: Boolean = true
)

@Composable
fun SelectTimeSlotScreen(
    onProceed: () -> Unit = {},
    onClose: () -> Unit = {},
    onBack: () -> Unit = {},
    onAddressClick: () -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf(1) } // Index of selected date
    var selectedTimeSlot by remember { mutableStateOf<String?>(null) }

    val dates = listOf(
        DateOption("Sat", 16),
        DateOption("Sun", 17),
        DateOption("Mon", 18)
    )

    val timeSlots = listOf(
        listOf(
            TimeSlot("07:00 AM"),
            TimeSlot("07:30 AM"),
            TimeSlot("08:00 AM", isHighDemand = true)
        ),
        listOf(
            TimeSlot("08:30 AM"),
            TimeSlot("09:00 AM"),
            TimeSlot("09:30 AM")
        ),
        listOf(
            TimeSlot("10:00 AM"),
            TimeSlot("10:30 AM"),
            TimeSlot("11:00 AM")
        ),
        listOf(
            TimeSlot("11:30 AM"),
            TimeSlot("12:00 PM"),
            TimeSlot("12:30 PM")
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Status Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column {
                    // Status Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "9:30",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = asyncImageVectorOrPlaceholder("drawable/ic_signal.xml"),
                                contentDescription = "Signal",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = asyncImageVectorOrPlaceholder("drawable/ic_battery.xml"),
                                contentDescription = "Battery",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = asyncImageVectorOrPlaceholder("drawable/ic_arrow_back.xml"),
                                    contentDescription = "Back",
                                    tint = Color.Black
                                )
                            }
                            Text(
                                text = "Summary",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = asyncImageVectorOrPlaceholder("drawable/ic_close.xml"),
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Address Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable(onClick = onAddressClick),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = asyncImageVectorOrPlaceholder("drawable/ic_home.xml"),
                                contentDescription = "Home",
                                tint = Color(0xFF6C4DFF),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Home - D 105, Kesnand Rd, opp. t...",
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                        Icon(
                            imageVector = asyncImageVectorOrPlaceholder("drawable/ic_arrow_right.xml"),
                            contentDescription = "Change",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Content Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Select date section
                        Text(
                            "Select date of service",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Your service will take approx. 2 hrs 20 mins",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Date Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            dates.forEachIndexed { index, date ->
                                DateCard(
                                    date = date,
                                    isSelected = selectedDate == index,
                                    onClick = { selectedDate = index },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Cancellation Policy
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = asyncImageVectorOrPlaceholder("drawable/ic_info_outline.xml"),
                                contentDescription = "Info",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Free cancellation till 2 hrs before the booked slot, post that ₹50 chargeable",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Time slot section
                        Text(
                            "Time to start the service",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Time Slots Grid
                        timeSlots.forEach { rowSlots ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowSlots.forEach { slot ->
                                    TimeSlotCard(
                                        timeSlot = slot,
                                        isSelected = selectedTimeSlot == slot.time,
                                        onClick = { selectedTimeSlot = slot.time },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Proceed Button at bottom
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = onProceed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C4DFF)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = selectedTimeSlot != null
            ) {
                Text(
                    "Proceed to checkout",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

//@Composable
//fun DateCard(
//    date: DateOption,
//    isSelected: Boolean,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = modifier
//            .clip(RoundedCornerShape(12.dp))
//            .background(
//                if (isSelected) Color(0xFFEDE7F6)
//                else Color.White
//            )
//            .border(
//                width = 2.dp,
//                color = if (isSelected) Color(0xFF6C4DFF) else Color(0xFFE0E0E0),
//                shape = RoundedCornerShape(12.dp)
//            )
//            .clickable(onClick = onClick)
//            .padding(vertical = 16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                date.day,
//                fontSize = 13.sp,
//                color = if (isSelected) Color(0xFF6C4DFF) else Color.Gray,
//                fontWeight = FontWeight.Medium
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                date.date.toString(),
//                fontSize = 24.sp,
//                color = if (isSelected) Color(0xFF6C4DFF) else Color.Black,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}

@Composable
fun TimeSlotCard(
    timeSlot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isSelected -> Color(0xFFEDE7F6)
                    !timeSlot.isAvailable -> Color(0xFFF5F5F5)
                    else -> Color.White
                }
            )
            .border(
                width = 1.5.dp,
                color = when {
                    isSelected -> Color(0xFF6C4DFF)
                    !timeSlot.isAvailable -> Color(0xFFE0E0E0)
                    else -> Color(0xFFE0E0E0)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = timeSlot.isAvailable, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                timeSlot.time,
                fontSize = 14.sp,
                color = when {
                    isSelected -> Color(0xFF6C4DFF)
                    !timeSlot.isAvailable -> Color.Gray
                    else -> Color.Black
                },
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            if (timeSlot.isHighDemand) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "High demand",
                        fontSize = 9.sp,
                        color = Color(0xFFFF6F00),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SelectTimeSlotScreenPreview() {
    SelectTimeSlotScreen()
}