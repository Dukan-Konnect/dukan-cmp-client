package org.example.project.home.presentation.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.core.resources.AppIcons
import org.jetbrains.compose.ui.tooling.preview.Preview

data class OrderItem(
    val id: String,
    val name: String,
    val rating: String,
    val duration: String,
    val price: Int,
    val oldPrice: Int? = null,
    val image: String,
    val quantity: Int = 1,
    val isPackage: Boolean = false,
    val packageItems: List<PackageService>? = null
)

data class PackageService(
    val name: String,
    val price: Int
)

@Composable
fun SummaryScreen(
    onBack: () -> Unit = {},
    onPay: () -> Unit = {},
    onEditOrder: (String) -> Unit = {},
    onCustomisePackage: (String) -> Unit = {},
    onCouponsClick: () -> Unit = {},
    onAddressChange: () -> Unit = {},
    onTimeChange: () -> Unit = {}
) {
    var orders by remember {
        mutableStateOf(
            listOf(
                OrderItem(
                    id = "1",
                    name = "Fruits Cleanup",
                    rating = "4.76 (978k)",
                    duration = "55 mins",
                    price = 500,
                    image = "drawable/service_cleanup.png",
                    quantity = 1
                ),
                OrderItem(
                    id = "2",
                    name = "Manvi's Package",
                    rating = "4.76 (978k)",
                    duration = "90 mins",
                    price = 800,
                    oldPrice = 950,
                    image = "drawable/package_manvi.png",
                    quantity = 1,
                    isPackage = true,
                    packageItems = listOf(
                        PackageService("Full body massage", 200),
                        PackageService("Head message", 100),
                        PackageService("Manicure", 200),
                        PackageService("Pedicure", 300)
                    )
                )
            )
        )
    }

    val itemTotal = orders.sumOf { it.price * it.quantity }
    val discount = 120
    val totalAmount = itemTotal - discount

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F7F7))) {
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
                                imageVector = AppIcons.placeholder,
                                contentDescription = "Signal",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = AppIcons.placeholder,
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = AppIcons.placeholder,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                        Text(
                            text = "Summary",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 120.dp)
            ) {
                // Plus Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
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
                                imageVector = AppIcons.placeholder,
                                contentDescription = "Plus",
                                tint = Color(0xFF6C4DFF),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Save 15% on every service",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    "Select your plan",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6C4DFF)
                                )
                            }
                        }
                        Icon(
                            imageVector = AppIcons.placeholder,
                            contentDescription = "Arrow",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Your orders section
                Text(
                    "Your orders",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Order Items
                orders.forEach { order ->
                    OrderItemCard(
                        order = order,
                        onQuantityChange = { newQuantity ->
                            orders = orders.map {
                                if (it.id == order.id) it.copy(quantity = newQuantity)
                                else it
                            }
                        },
                        onEdit = { onEditOrder(order.id) },
                        onCustomise = { onCustomisePackage(order.id) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Coupons and offers
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable(onClick = onCouponsClick),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = AppIcons.placeholder,
                                contentDescription = "Coupon",
                                tint = Color(0xFF00C853),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Coupons and offers",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "5 offers",
                                fontSize = 14.sp,
                                color = Color(0xFF6C4DFF),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = AppIcons.placeholder,
                                contentDescription = "Arrow",
                                tint = Color(0xFF6C4DFF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Payment summary",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Item total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Item total",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                "₹$itemTotal",
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Item discount
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Item discount",
                                fontSize = 14.sp,
                                color = Color(0xFF00C853)
                            )
                            Text(
                                "-₹$discount",
                                fontSize = 14.sp,
                                color = Color(0xFF00C853),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            color = Color(0xFFE0E0E0)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Address
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onAddressChange),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = AppIcons.placeholder,
                                    contentDescription = "Home",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Home - D 105, Kesnand Rd, opp. t...",
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    maxLines = 1
                                )
                            }
                            Icon(
                                imageVector = AppIcons.placeholder,
                                contentDescription = "Edit",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Date and Time
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onTimeChange),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = AppIcons.placeholder,
                                    contentDescription = "Time",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Sun, Jul 17 - 07:00 AM",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                            Icon(
                                imageVector = AppIcons.placeholder,
                                contentDescription = "Edit",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Payment Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = onPay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C4DFF)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Pay ₹${totalAmount}",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    buildAnnotatedString {
                        append("By proceeding, you agree to our ")
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append("T&C")
                        }
                        append(", ")
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append("Privacy")
                        }
                        append(" and ")
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append("Cancellation policy")
                        }
                    },
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun OrderItemCard(
    order: OrderItem,
    onQuantityChange: (Int) -> Unit,
    onEdit: () -> Unit,
    onCustomise: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Service Image
                Image(
                    imageVector = AppIcons.placeholder,
                    contentDescription = order.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Service Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        order.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = AppIcons.placeholder,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${order.rating} | ${order.duration}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (order.isPackage) {
                        Text(
                            "Customise",
                            fontSize = 13.sp,
                            color = Color(0xFF6C4DFF),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = onCustomise)
                        )
                    } else {
                        Text(
                            "Edit",
                            fontSize = 13.sp,
                            color = Color(0xFF6C4DFF),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = onEdit)
                        )
                    }
                }

                // Price and Quantity
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(1.dp, Color(0xFF6C4DFF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "-",
                            fontSize = 18.sp,
                            color = Color(0xFF6C4DFF),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                if (order.quantity > 1) onQuantityChange(order.quantity - 1)
                            }
                        )
                        Text(
                            order.quantity.toString(),
                            fontSize = 14.sp,
                            color = Color(0xFF6C4DFF),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "+",
                            fontSize = 18.sp,
                            color = Color(0xFF6C4DFF),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onQuantityChange(order.quantity + 1) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (order.oldPrice != null) {
                        Text(
                            "₹${order.oldPrice}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            style = androidx.compose.ui.text.TextStyle(
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                    }
                    Text(
                        "₹ ${order.price}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // Package items
            if (order.isPackage && order.packageItems != null) {
                Spacer(modifier = Modifier.height(12.dp))
                order.packageItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color.Gray, shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            item.name,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "₹ ${item.price}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SummaryScreenPreview() {
    SummaryScreen()
}