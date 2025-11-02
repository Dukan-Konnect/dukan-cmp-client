package org.example.project.home.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.example.project.core.resources.AppIcons
import org.example.project.home.domain.model.CategoryItem
import org.example.project.home.presentation.viewmodels.ServiceDetailsEffect
import org.example.project.home.presentation.viewmodels.ServiceDetailsEvent
import org.example.project.home.presentation.viewmodels.ServiceDetailsUiState
import org.example.project.home.presentation.viewmodels.ServiceDetailsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ServiceDetailScreen(
    serviceId: Int = 1,
    onBackClick: () -> Unit = {},
    onSubServiceClick: (Int) -> Unit = {},
    viewModel: ServiceDetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Load service details on first composition
    LaunchedEffect(serviceId) {
        viewModel.onEvent(ServiceDetailsEvent.LoadService(serviceId))
    }

    // Collect one-off effects
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ServiceDetailsEffect.NavigateBack -> onBackClick()
                is ServiceDetailsEffect.NavigateToSubServiceDetails -> onSubServiceClick(effect.subServiceId)
                is ServiceDetailsEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    // Intent dispatcher
    val intent: (ServiceDetailsEvent) -> Unit = viewModel::onEvent

    ServiceDetailsScreenContent(
        uiState = uiState,
        intent = intent,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ServiceDetailsScreenContent(
    uiState: ServiceDetailsUiState,
    intent: (ServiceDetailsEvent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var cleanupExpanded by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
        ) {
            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            // Error state
            uiState.errorMessage?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { intent(ServiceDetailsEvent.Retry) }) {
                            Text("Retry")
                        }
                    },
                    dismissAction = {
                        TextButton(onClick = { intent(ServiceDetailsEvent.ErrorDismissed) }) {
                            Text("Dismiss")
                        }
                    }
                ) { Text(error) }
            }

            val serviceDetails = uiState.serviceDetails ?: return@Column

            // Header
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
                                imageVector = AppIcons.signal,
                                contentDescription = "Signal",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = AppIcons.battery,
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
                        IconButton(onClick = { intent(ServiceDetailsEvent.BackClicked) }) {
                            Icon(
                                imageVector = AppIcons.arrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                        Text(
                            text = serviceDetails.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
            ) {
                // Banner Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    if (serviceDetails.bannerImage.isNotEmpty()) {
                        AsyncImage(
                            model = serviceDetails.bannerImage,
                            contentDescription = serviceDetails.bannerTitle,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            imageVector = AppIcons.placeholder,
                            contentDescription = serviceDetails.bannerTitle,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            serviceDetails.bannerTitle,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Info icon
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.info,
                            contentDescription = "Info",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Service Info Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                serviceDetails.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = AppIcons.heartOutline,
                                    contentDescription = "Heart",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "UC Safe",
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = AppIcons.star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFA000),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                serviceDetails.ratingText,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = AppIcons.calendar,
                                contentDescription = "Calendar",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                serviceDetails.bookingsText,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        // Custom Package Card
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                .clickable { }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = AppIcons.placeholder,
                                    contentDescription = "Package",
                                    tint = Color(0xFF00C853),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Create a Custom Package",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                    Text(
                                        "Specifically for your needs",
                                        fontSize = 12.sp,
                                        color = Color.Gray
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
                }

                // Categories Section
                if (serviceDetails.categories.isNotEmpty()) {
                    Text(
                        "Categories",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        serviceDetails.categories.forEach { category ->
                            CategoryItem(
                                category = category,
                                isSelected = uiState.selectedCategory?.id == category.id,
                                onClick = { intent(ServiceDetailsEvent.CategorySelected(category.id)) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Service Sections
                uiState.filteredSections.forEach { section ->
                    ExpandableServiceSection(
                        title = section.title,
                        expanded = cleanupExpanded,
                        onExpandChange = { cleanupExpanded = it }
                    ) {
                        section.items.forEach { subService ->
                            SubServiceItem(
                                subService = subService,
                                onClick = { intent(ServiceDetailsEvent.SubServiceClicked(subService.id)) }
                            )
                        }
                    }
                }
            }
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

@Composable
fun CategoryItem(
    category: CategoryItem,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF6C4DFF).copy(alpha = 0.1f) else Color.Transparent)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) Color(0xFF6C4DFF) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (category.image.isNotEmpty()) {
                AsyncImage(
                    model = category.image,
                    contentDescription = category.label,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = AppIcons.placeholder,
                    contentDescription = category.label,
                    modifier = Modifier.size(32.dp),
                    tint = if (isSelected) Color(0xFF6C4DFF) else Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            category.label,
            fontSize = 11.sp,
            color = if (isSelected) Color(0xFF6C4DFF) else Color.Black,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun ExpandableServiceSection(
    title: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandChange(!expanded) }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Icon(
                    imageVector = if(expanded) AppIcons.placeholder else AppIcons.placeholder,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            if (expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun SubServiceItem(
    subService: org.example.project.home.domain.model.SubService,
    onClick: () -> Unit
) {
    var quantity by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (subService.image.isNotEmpty()) {
                AsyncImage(
                    model = subService.image,
                    contentDescription = subService.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = AppIcons.placeholder,
                    contentDescription = subService.title,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                subService.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = AppIcons.star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFA000),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    subService.ratingText,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "₹ ${subService.price}",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        if (quantity > 0) {
            Row(
                modifier = Modifier
                    .border(1.dp, Color(0xFF6C4DFF), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "-",
                    fontSize = 18.sp,
                    color = Color(0xFF6C4DFF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { quantity = (quantity - 1).coerceAtLeast(0) }
                )
                Text(
                    quantity.toString(),
                    fontSize = 14.sp,
                    color = Color(0xFF6C4DFF),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "+",
                    fontSize = 18.sp,
                    color = Color(0xFF6C4DFF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { quantity++ }
                )
            }
        } else {
            Button(
                onClick = { quantity = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6C4DFF)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6C4DFF)),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    "Add",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@Preview
@Composable
fun SalonClassicScreenPreview() {
    ServiceDetailScreen()
}