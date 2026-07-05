package org.example.project.home.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.ic_arrow_back
import dukaankonnect.composeapp.generated.resources.ic_calendar_clock
import dukaankonnect.composeapp.generated.resources.ic_edit
import dukaankonnect.composeapp.generated.resources.ic_location
import dukaankonnect.composeapp.generated.resources.ic_phone
import dukaankonnect.composeapp.generated.resources.ic_star
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
import org.example.project.core.utils.AddressFormatter
import org.example.project.home.presentation.navigation.SummaryRoute
import org.example.project.home.presentation.viewmodels.SummaryEffect
import org.example.project.home.presentation.viewmodels.SummaryEvent
import org.example.project.home.presentation.viewmodels.SummaryViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    onPay: (String) -> Unit = {},
    onCouponsClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val addressValue = state.address.takeIf { it.isNotBlank() }

    var showAddressSheet by remember { mutableStateOf(false) }
    var showTimeSlotSheet by remember { mutableStateOf(false) }
    var showPhoneSheet by remember { mutableStateOf(false) }

    var paymentOrderId by remember { mutableStateOf<String?>(null) }
    var paymentAmount by remember { mutableStateOf(0L) }
    var paymentPhoneNumber by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        println("[SummaryScreen] collecting viewModel effects")
        viewModel.effect.collect { effect ->
            println("[SummaryScreen] effect received=$effect")
            when (effect) {
                SummaryEffect.NavigateBack -> onBack()
                is SummaryEffect.NavigateToBookings -> {
                    println("[SummaryScreen] NavigateToBookings message=${effect.message}")
                    onPay(effect.message)
                }
                is SummaryEffect.NavigateToPayment -> {
                    println("[SummaryScreen] NavigateToPayment orderId=${effect.orderId} amount=${effect.amount} phone=${effect.phoneNumber}")
                    paymentOrderId = effect.orderId
                    paymentAmount = effect.amount
                    paymentPhoneNumber = effect.phoneNumber
                }

                is SummaryEffect.ShowMessage -> launch {
                    println("[SummaryScreen] ShowMessage=${effect.message}")
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    paymentOrderId?.let { orderId ->
        println("[SummaryScreen] LaunchPaymentActivity orderId=$orderId amount=$paymentAmount phone=$paymentPhoneNumber")
        LaunchPaymentActivity(
            orderId = orderId,
            amount = paymentAmount,
            phoneNumber = paymentPhoneNumber,
            onResult = { success ->
                println("[SummaryScreen] payment result orderId=$orderId success=$success")
                paymentOrderId = null
                if (success) {
                    viewModel.onEvent(SummaryEvent.PaymentSucceeded(orderId))
                } else {
                    coroutineScope.launch {
                        println("[SummaryScreen] payment failed snackbar")
                        snackbarHostState.showSnackbar("Payment failed")
                    }
                }
            }
        )
    }

    val booking = state.booking
    if (booking == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Booking details are unavailable")
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onEvent(SummaryEvent.BackClicked) }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Your order",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                OrderItemRow(order = booking)

                Spacer(modifier = Modifier.height(16.dp))

                PhoneRow(
                    name = state.customerName.ifBlank { "You" },
                    phoneNumber = state.phoneNumber.ifBlank { "Add phone number" },
                    onEditClick = { showPhoneSheet = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                        SummaryRow(
                            label = "Item total",
                            value = viewModel.formatPrice(state.itemTotalCents)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryRow(
                            label = "Tax (5%)",
                            value = viewModel.formatPrice(state.taxesCents)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryRow(
                            label = "Total amount",
                            value = viewModel.formatPrice(state.amountToPayCents),
                            highlight = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val addressText = AddressFormatter.formatFullAddress(addressValue)
                    val timeSlotText = state.timeSlotFormatted

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
                                painter = painterResource(Res.drawable.ic_location),
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
                                painter = painterResource(Res.drawable.ic_edit),
                                contentDescription = "Edit address",
                                tint = Color(0xFF6C4DFF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

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
                                    painter = painterResource(Res.drawable.ic_calendar_clock),
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
                                    painter = painterResource(Res.drawable.ic_edit),
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
                            text = if (hasTimeSlot) {
                                "Pay ${viewModel.formatPrice(state.amountToPayCents)}"
                            } else {
                                "Select date and time"
                            },
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        if (showAddressSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddressSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.White
            ) {
                AddAddressBottomSheetContent(
                    currentAddress = addressValue,
                    onSave = { newAddress ->
                        viewModel.onEvent(SummaryEvent.UpdateAddress(newAddress))
                        showAddressSheet = false
                        if (state.timeSlotFormatted.isNullOrBlank()) {
                            showTimeSlotSheet = true
                        }
                    },
                    onDismiss = { showAddressSheet = false }
                )
            }
        }

        if (showTimeSlotSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTimeSlotSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.White
            ) {
                SelectTimeSlotBottomSheetContent(
                    currentTimeSlot = state.timeSlotFormatted,
                    onSelect = { isoString, formattedString ->
                        viewModel.onEvent(SummaryEvent.UpdateTimeSlot(isoString, formattedString))
                        showTimeSlotSheet = false
                    },
                    onDismiss = { showTimeSlotSheet = false }
                )
            }
        }

        if (showPhoneSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPhoneSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.White
            ) {
                EditPhoneBottomSheetContent(
                    currentPhoneNumber = state.phoneNumber,
                    currentName = state.customerName,
                    onSave = { newPhone, newName ->
                        viewModel.onEvent(SummaryEvent.UpdatePhoneNumber(newPhone))
                        viewModel.onEvent(SummaryEvent.UpdateName(newName))
                        showPhoneSheet = false
                    },
                    onDismiss = { showPhoneSheet = false }
                )
            }
        }
    }
}

@Composable
private fun OrderItemRow(order: SummaryRoute) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = order.subServiceImage,
                    contentDescription = order.subServiceTitle,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.subServiceTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.serviceTitle,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₹ ${order.providerFee}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = order.providerImageUrl,
                    contentDescription = order.providerName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.providerName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_star),
                            contentDescription = "Rating",
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.providerRating.toString(),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal,
            color = if (highlight) Color.Black else Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
private fun PhoneRow(
    name: String,
    phoneNumber: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_phone),
                contentDescription = "Phone",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Text(phoneNumber, fontSize = 13.sp, color = Color.Gray)
            }
            TextButton(onClick = onEditClick) {
                Text("Edit")
            }
        }
    }
}

@Composable
private fun EditPhoneBottomSheetContent(
    currentPhoneNumber: String,
    currentName: String,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember(currentName) { mutableStateOf(currentName) }
    var phone by remember(currentPhoneNumber) { mutableStateOf(currentPhoneNumber) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Contact details", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.filter(Char::isDigit).take(10) },
            label = { Text("Phone number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onSave(phone, name) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF))
            ) {
                Text("Save")
            }

            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun AddAddressBottomSheetContent(
    currentAddress: String?,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var address by remember(currentAddress) { mutableStateOf(currentAddress.orEmpty()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Delivery address", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            colors = OutlinedTextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onSave(address) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF))
            ) {
                Text("Save")
            }

            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun SelectTimeSlotBottomSheetContent(
    currentTimeSlot: String?,
    onSelect: (isoString: String, formattedString: String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentMoment = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    val today = currentMoment.date

    val dates = remember { (0..6).map { today.plus(it, DateTimeUnit.DAY) } }

    val timeSlots = remember {
        listOf(
            LocalTime(8, 0),
            LocalTime(10, 0),
            LocalTime(12, 0),
            LocalTime(14, 0),
            LocalTime(16, 0),
            LocalTime(18, 0)
        )
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

    var selectedDate by remember(currentTimeSlot) {
        mutableStateOf(
            if (currentTimeSlot != null) {
                val datePart = currentTimeSlot.substringBefore(",").trim()
                dates.find { it.format(dateFormatter) == datePart } ?: today
            } else {
                today
            }
        )
    }

    var selectedTime by remember(currentTimeSlot) {
        mutableStateOf(
            if (currentTimeSlot != null) {
                val timePart = currentTimeSlot.substringAfter(",").trim()
                timeSlots.find { it.format(timeFormatter) == timePart }
            } else {
                null
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select date and time", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Your service will take approx. 45 mins", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(dates) { date ->
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
                    color = if (isSelected) Color(0xFFECE7FF) else Color.White,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF6C4DFF) else Color(0xFFE0E0E0)
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
                            color = if (isSelected) Color.Black else Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dayOfMonth,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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

                        val isPastTime = slotInstant < Clock.System.now().plus(45.minutes)
                        val isSelected = time == selectedTime

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clickable(enabled = !isPastTime) {
                                    selectedTime = time
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFECE7FF) else if (isPastTime) Color(0xFFF9F9F9) else Color.White,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF6C4DFF) else if (isPastTime) Color.Transparent else Color(0xFFE0E0E0)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = time.format(timeFormatter),
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isPastTime) Color.LightGray else Color.Black
                                )
                            }
                        }
                    }

                    repeat(3 - rowTimes.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (selectedTime != null) {
                        val finalDateTime = LocalDateTime(
                            year = selectedDate.year,
                            monthNumber = selectedDate.monthNumber,
                            dayOfMonth = selectedDate.dayOfMonth,
                            hour = selectedTime!!.hour,
                            minute = selectedTime!!.minute
                        )
                        val isoString = finalDateTime.toInstant(TimeZone.currentSystemDefault()).toString()

                        val formattedSlot = "${selectedDate.format(dateFormatter)}, ${selectedTime!!.format(timeFormatter)}"

                        onSelect(isoString, formattedSlot)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = selectedTime != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF))
            ) {
                Text("Confirm")
            }
        }
    }
}
