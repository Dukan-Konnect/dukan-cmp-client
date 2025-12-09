package org.example.project.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.example.project.core.resources.AppIcons
import org.example.project.home.domain.model.Booking
import org.example.project.home.domain.model.BookingStatus
import org.example.project.home.presentation.viewmodels.BookingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingsScreen(
    viewModel: BookingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Bookings",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
        }

        // Content
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6C4DFF))
            }
        } else if (state.bookings.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.placeholder,
                        contentDescription = "No bookings",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No bookings yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Your bookings will appear here",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            // Bookings list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.bookings) { booking ->
                    BookingCard(booking = booking)
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Booking header with status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${booking.orderId.takeLast(8)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                StatusChip(status = booking.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Service name
            Text(
                text = booking.subServiceName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Provider info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = booking.providerImage,
                    contentDescription = booking.providerName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        booking.providerName,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        booking.providerPhone,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            booking.providerRating.toString(),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "₹ ${booking.totalAmountCents / 100}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Booking details
            Divider(color = Color(0xFFE0E0E0))

            Spacer(modifier = Modifier.height(12.dp))

            // Date and time
            if (!booking.scheduledDate.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.placeholder,
                        contentDescription = "Time",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        booking.scheduledDate,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            // Address
            if (!booking.address.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.placeholder,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        booking.address,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }
            }

            // Booking date
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Booked on ${formatDate(booking.bookingDate)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun StatusChip(status: BookingStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        BookingStatus.PENDING -> Triple(Color(0xFFFFF3E0), Color(0xFFFF6F00), "Pending")
        BookingStatus.CONFIRMED -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Confirmed")
        BookingStatus.IN_PROGRESS -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "In Progress")
        BookingStatus.COMPLETED -> Triple(Color(0xFFE0F2F1), Color(0xFF00695C), "Completed")
        BookingStatus.CANCELLED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Cancelled")
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

