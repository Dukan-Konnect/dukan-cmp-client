package org.example.project.home.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.core.resources.AppIcons
import org.example.project.home.presentation.viewmodels.SummaryEffect
import org.example.project.home.presentation.viewmodels.SummaryEvent
import org.example.project.home.presentation.viewmodels.SummaryViewModel
import org.example.project.home.utils.AddressFormatter
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    onPay: () -> Unit = {},
    onEditOrder: (String) -> Unit = {},
    onCustomisePackage: (String) -> Unit = {},
    onCouponsClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddressSheet by remember { mutableStateOf(false) }
    var showTimeSlotSheet by remember { mutableStateOf(false) }
    var showPhoneSheet by remember { mutableStateOf(false) }

    var paymentOrderId by remember { mutableStateOf<String?>(null) }
    var paymentAmount by remember { mutableStateOf(0L) }
    var paymentPhoneNumber by remember { mutableStateOf("") }

    // Collect effects and handle navigation/messages
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SummaryEffect.NavigateBack -> onBack()
                is SummaryEffect.NavigateToPayment -> {
                    paymentOrderId = effect.orderId
                    paymentAmount = effect.amount
                    paymentPhoneNumber = state.cartSummary?.phoneNumber ?: ""
                }
                is SummaryEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    // Launch PaymentActivity when orderId is set
    paymentOrderId?.let { orderId ->
        LaunchPaymentActivity(
            orderId = orderId,
            amount = paymentAmount,
            phoneNumber = paymentPhoneNumber,
            onResult = { success ->
                paymentOrderId = null
                if (success) {
                    // Payment successful - you can add success handling here
                    onPay()
                } else {
                    // Payment failed or cancelled
                    viewModel.onEvent(SummaryEvent.ErrorDismissed)
                }
            }
        )
    }

    val orders = state.cartItems.map { cartItem ->
        // Map CartItem domain model to local OrderItem UI model used in this file
        OrderItem(
            id = cartItem.productId,
            name = cartItem.name,
            rating = "",
            duration = "",
            price = (cartItem.priceCents / 100).toInt(),
            oldPrice = null,
            image = cartItem.imageUrl ?: "",
            quantity = cartItem.quantity,
            isPackage = false,
            packageItems = null
        )
    }

    val totals = state.cartTotals
    val itemTotal = totals?.itemTotalCents?.let { viewModel.formatPrice(it) }
        ?: viewModel.formatPrice(orders.sumOf { it.price * it.quantity }.toLong() * 100)
    val taxAmount = totals?.taxesCents?.let { viewModel.formatPrice(it) } ?: "₹0"
    val totalAmount = totals?.amountToPayCents?.let { viewModel.formatPrice(it) } ?: itemTotal

    // Handle loading and empty states
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF6C4DFF))
        }
        return
    }

    if (state.isCartEmpty) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = AppIcons.placeholder,
                    contentDescription = "Empty cart",
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Your cart is empty",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Add items to get started",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF))
                ) {
                    Text("Browse Services")
                }
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F7F7))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Status Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column {

                    // Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewModel.onEvent(SummaryEvent.BackClicked)
                            onBack()
                        }) {
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
                    OrderItemRow(
                        order = order,
                        onQuantityChange = { newQuantity ->
                            viewModel.onEvent(SummaryEvent.UpdateItemQuantity(order.id, newQuantity))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Phone row
                PhoneRow(
                    name = state.cartSummary?.name ?: "You",
                    phoneNumber = state.cartSummary?.phoneNumber ?: "1234567890",
                    onEditClick = { showPhoneSheet = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Payment summary card remains but uses itemTotal / taxAmount / totalAmount
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
                                itemTotal,
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tax
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Tax",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                taxAmount,
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Total amount (final amount including taxes)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total amount",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Text(
                                totalAmount,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom bar overlay
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val addressText = AddressFormatter.formatFullAddress(state.cartSummary?.address)
                val timeSlotText = state.cartSummary?.timeSlot

                // Address row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddressSheet = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.placeholder, // address icon
                            contentDescription = "Address",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            addressText,
                            fontSize = 14.sp,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = { showAddressSheet = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.edit, // edit/pencil icon - use placeholder for now
                            contentDescription = "Edit address",
                            tint = Color(0xFF6C4DFF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Time slot row (only when selected)
                if (!timeSlotText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimeSlotSheet = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = AppIcons.placeholder, // time icon
                                contentDescription = "Time slot",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                timeSlotText,
                                fontSize = 14.sp,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = { showTimeSlotSheet = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = AppIcons.edit, // edit/pencil icon - use placeholder for now
                                contentDescription = "Edit time slot",
                                tint = Color(0xFF6C4DFF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val hasTimeSlot = !timeSlotText.isNullOrBlank()
                Button(
                    onClick = {
                        if (hasTimeSlot) {
                            viewModel.onEvent(SummaryEvent.ProceedToPayment)
                        } else {
                            showTimeSlotSheet = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF))
                ) {
                    Text(
                        text = if (hasTimeSlot) "Pay $totalAmount" else "Select time slot",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Address Bottom Sheet
        if (showAddressSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddressSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.White
            ) {
                AddAddressBottomSheetContent(
                    currentAddress = state.cartSummary?.address,
                    onSave = { newAddress ->
                        viewModel.onEvent(SummaryEvent.UpdateAddress(newAddress))
                        showAddressSheet = false
                        // After saving address, optionally open time slot selector
                        if (state.cartSummary?.timeSlot.isNullOrBlank()) {
                            showTimeSlotSheet = true
                        }
                    },
                    onDismiss = { showAddressSheet = false }
                )
            }
        }

        // Time Slot Bottom Sheet
        if (showTimeSlotSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTimeSlotSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.White
            ) {
                SelectTimeSlotBottomSheetContent(
                    currentTimeSlot = state.cartSummary?.timeSlot,
                    onSelect = { selectedSlot ->
                        viewModel.onEvent(SummaryEvent.UpdateTimeSlot(selectedSlot))
                        showTimeSlotSheet = false
                    },
                    onDismiss = { showTimeSlotSheet = false }
                )
            }
        }

        // Phone Number Bottom Sheet
        if (showPhoneSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPhoneSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.White
            ) {
                EditPhoneBottomSheetContent(
                    currentPhoneNumber = state.cartSummary?.phoneNumber ?: "",
                    currentName = state.cartSummary?.name,
                    onSave = { newPhone, newName ->
                        viewModel.onEvent(SummaryEvent.UpdatePhoneNumber(newPhone))
                        // TODO: add event to store name when backend/storage is ready
                        showPhoneSheet = false
                    },
                    onDismiss = { showPhoneSheet = false }
                )
            }
        }

        // Snackbar for messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
        )
    }
}

@Composable
fun OrderItemRow(
    order: OrderItem,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Single line: name, quantity, price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quantity controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(1.dp, Color(0xFF6C4DFF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            order.quantity.toString(),
                            fontSize = 14.sp,
                            color = Color(0xFF6C4DFF),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "+",
                            fontSize = 18.sp,
                            color = Color(0xFF6C4DFF),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                onQuantityChange(order.quantity + 1)
                            }
                        )
                    }

                    Text(
                        text = "₹ ${order.price}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneRow(
    name: String,
    phoneNumber: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onEditClick() },
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
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = AppIcons.placeholder, // phone icon
                    contentDescription = "Phone",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$name, +91-$phoneNumber",
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TextButton(onClick = onEditClick) {
                Text(
                    text = "Change",
                    color = Color(0xFF6C4DFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun AddAddressBottomSheetContent(
    currentAddress: String?,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Initialize from currentAddress by attempting to split into parts: house, street, landmark
    var houseNumber by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var streetAddress by remember { mutableStateOf("") }

    LaunchedEffect(currentAddress) {
        currentAddress?.let { full ->
            val parts = full.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            when (parts.size) {
                0 -> {}
                1 -> streetAddress = parts[0]
                2 -> {
                    houseNumber = parts[0]
                    streetAddress = parts[1]
                }
                else -> {
                    houseNumber = parts[0]
                    streetAddress = parts[1]
                    // Everything after first two parts is treated as landmark/extra info
                    landmark = parts.drop(2).joinToString(", ")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            "Add Delivery Address",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = streetAddress,
            onValueChange = { streetAddress = it },
            label = { Text("Street Address") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6C4DFF),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedLabelColor = Color(0xFF6C4DFF)
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = houseNumber,
            onValueChange = { houseNumber = it },
            label = { Text("House/Flat Number") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6C4DFF),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedLabelColor = Color(0xFF6C4DFF)
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = landmark,
            onValueChange = { landmark = it },
            label = { Text("Landmark (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6C4DFF),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedLabelColor = Color(0xFF6C4DFF)
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF6C4DFF)
                ),
                border = BorderStroke(1.dp, Color(0xFF6C4DFF))
            ) {
                Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = {
                    val fullAddress = buildString {
                        if (houseNumber.isNotBlank()) append("$houseNumber, ")
                        append(streetAddress)
                        if (landmark.isNotBlank()) append(", $landmark")
                    }
                    onSave(fullAddress)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C4DFF)
                ),
                enabled = streetAddress.isNotBlank()
            ) {
                Text("Save", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun EditPhoneBottomSheetContent(
    currentPhoneNumber: String,
    currentName: String?,
    onSave: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf(currentPhoneNumber) }
    var name by remember { mutableStateOf(currentName.orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        // Header with close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Contact for booking updates",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Professional will contact at this number, and a tracking link will be shared",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = AppIcons.close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Phone number input with country code
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Country code dropdown
            OutlinedTextField(
                value = "+91",
                onValueChange = {},
                modifier = Modifier.width(85.dp),
                enabled = false,
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color(0xFFE0E0E0),
                    disabledTextColor = Color.Black,
                    disabledContainerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        imageVector = AppIcons.arrowDown,
                        contentDescription = "Country code",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )

            // Phone number input
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    // Only allow digits and limit to 10 characters
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        phoneNumber = it
                    }
                },
                modifier = Modifier.weight(1f),
                label = { Text("Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C4DFF),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedLabelColor = Color(0xFF6C4DFF)
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        imageVector = AppIcons.placeholder, // contact icon
                        contentDescription = "Contact",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name input
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6C4DFF),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedLabelColor = Color(0xFF6C4DFF)
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Save button
        Button(
            onClick = {
                if (phoneNumber.length == 10) {
                    onSave(phoneNumber, name.ifBlank { null })
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C4DFF)
            ),
            enabled = phoneNumber.length == 10
        ) {
            Text(
                "Save details",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SelectTimeSlotBottomSheetContent(
    currentTimeSlot: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(0) }
    var selectedTimeSlot by remember { mutableStateOf(currentTimeSlot) }

    val dates = listOf(
        DateOption("Today", 14, "Nov"),
        DateOption("Tomorrow", 15, "Nov"),
        DateOption("Sat", 16, "Nov")
    )

    val timeSlots = listOf(
        listOf("08:00 AM", "08:30 AM", "09:00 AM"),
        listOf("09:30 AM", "10:00 AM", "10:30 AM"),
        listOf("11:00 AM", "11:30 AM", "12:00 PM"),
        listOf("12:30 PM", "01:00 PM", "01:30 PM")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Select Time Slot",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Your service will take approx. 2 hrs",
            fontSize = 14.sp,
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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Available time slots",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
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
                    TimeSlotChip(
                        time = slot,
                        isSelected = selectedTimeSlot == slot,
                        onClick = { selectedTimeSlot = slot },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF6C4DFF)
                ),
                border = BorderStroke(1.dp, Color(0xFF6C4DFF))
            ) {
                Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = {
                    selectedTimeSlot?.let { slot ->
                        val dateLabel = dates[selectedDate]
                        val fullSlot = "${dateLabel.day}, ${dateLabel.date} ${dateLabel.month} - $slot"
                        onSelect(fullSlot)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C4DFF)
                ),
                enabled = selectedTimeSlot != null
            ) {
                Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TimeSlotChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFEDE7F6) else Color.White)
            .border(
                width = 1.5.dp,
                color = if (isSelected) Color(0xFF6C4DFF) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            time,
            fontSize = 14.sp,
            color = if (isSelected) Color(0xFF6C4DFF) else Color.Black,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}



@Composable
fun DateCard(
    date: DateOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Color(0xFFEDE7F6)
                else Color.White
            )
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF6C4DFF) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                date.day,
                fontSize = 13.sp,
                color = if (isSelected) Color(0xFF6C4DFF) else Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                date.date.toString(),
                fontSize = 24.sp,
                color = if (isSelected) Color(0xFF6C4DFF) else Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun SummaryScreenPreview() {
    SummaryScreen(onBack = {}, onPay = {})
}