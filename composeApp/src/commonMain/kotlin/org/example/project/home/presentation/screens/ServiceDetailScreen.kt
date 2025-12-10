package org.example.project.home.presentation.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.example.project.core.resources.AppIcons
import org.example.project.home.domain.model.CategoryItem
import org.example.project.home.domain.model.ServiceDetails
import org.example.project.home.domain.model.ServiceSection
import org.example.project.home.domain.model.SubService
import org.example.project.home.domain.model.ServiceProvider
import org.example.project.home.presentation.viewmodels.ServiceDetailsEffect
import org.example.project.home.presentation.viewmodels.ServiceDetailsEvent
import org.example.project.home.presentation.viewmodels.ServiceDetailsUiState
import org.example.project.home.presentation.viewmodels.ServiceDetailsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ServiceDetailScreen(
    serviceId: Long = 1L,
    onBackClick: () -> Unit = {},
    onSubServiceClick: (String) -> Unit = {},
    onNavigateToSummary: () -> Unit = {},
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
                ServiceDetailsEffect.NavigateToSummary -> onNavigateToSummary()
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
    // Track expanded state for each section by section ID
    val expandedSections = remember { mutableStateMapOf<String, Boolean>() }

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

                    AsyncImage(
                        model = serviceDetails.bannerImage,
                        contentDescription = serviceDetails.bannerTitle,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )


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
                                "4.7(3k)",//serviceDetails.ratingText,
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
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Categories",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                serviceDetails.categories.forEach { category ->
                                    CategoryItem(
                                        category = category,
                                        onClick = {
                                            // Open the corresponding dropdown section
                                            expandedSections[category.id] = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Service Sections
                uiState.filteredSections.forEach { section ->
                    ExpandableServiceSection(
                        title = section.title,
                        expanded = expandedSections[section.id] ?: false,
                        onExpandChange = { expandedSections[section.id] = it }
                    ) {
                        section.items.forEach { subService ->
                            val selectedProvider = uiState.selectedProviders[subService.id]
                            val availableProviders = uiState.availableProviders[subService.id]
                            val isExpanded = uiState.expandedSubservices.contains(subService.id)

                            SubServiceItem(
                                subService = subService,
                                selectedProvider = selectedProvider,
                                availableProviders = availableProviders,
                                isExpanded = isExpanded,
                                onToggleDropdown = { intent(ServiceDetailsEvent.ToggleProviderDropdown(subService.id)) },
                                onSelectProvider = { provider -> intent(ServiceDetailsEvent.SelectProvider(subService.id, provider)) },
                                onRemoveProvider = { intent(ServiceDetailsEvent.RemoveProvider(subService.id)) },
                                onClick = { intent(ServiceDetailsEvent.SubServiceClicked(subService.id)) }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Cart Bar (visible when item count > 0)
        if (uiState.screenCartItemCount > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "₹ ${uiState.screenCartTotalCents / 100}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Text(
                            text = "${uiState.screenCartItemCount} Item${if (uiState.screenCartItemCount == 1) "" else "s"}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Button(
                        onClick = { intent(ServiceDetailsEvent.ViewCartClicked) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("View Cart", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (uiState.screenCartItemCount > 0) 72.dp else 16.dp)
        )
    }
}

@Composable
fun CategoryItem(
    category: CategoryItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
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
                    tint = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            category.label,
            fontSize = 11.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Normal
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
                    imageVector = if(expanded) AppIcons.arrowUp else AppIcons.arrowDown,
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
    subService: SubService,
    selectedProvider: ServiceProvider?,
    availableProviders: List<ServiceProvider>?,
    isExpanded: Boolean,
    onToggleDropdown: () -> Unit,
    onSelectProvider: (ServiceProvider) -> Unit,
    onRemoveProvider: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        // Main Row with SubService Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (subService.image.isNotEmpty()) {
                    AsyncImage(
                        model = subService.image,
                        contentDescription = subService.title,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = AppIcons.placeholder,
                        contentDescription = subService.title,
                        modifier = Modifier.size(25.dp),
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
            }

            // Add/Remove Provider Button
            if (selectedProvider == null) {
                Button(
                    onClick = onToggleDropdown,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Add", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Button(
                    onClick = onRemoveProvider,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935), contentColor = Color.White),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Remove", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Provider Dropdown
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            if (availableProviders == null) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally))
            } else if (availableProviders.isEmpty()) {
                Text(
                    "No providers available",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF7F7F7), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableProviders.forEach { provider ->
                        ProviderItem(
                            provider = provider,
                            isSelected = selectedProvider?.id == provider.id,
                            isEnabled = selectedProvider == null || selectedProvider.id == provider.id,
                            onSelect = { onSelectProvider(provider) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderItem(
    provider: ServiceProvider,
    isSelected: Boolean,
    isEnabled: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) Color(0xFFE8F5E9) else Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Provider Image
            AsyncImage(
                model = provider.imageUrl,
                contentDescription = provider.name,
                modifier = Modifier
                    .width(45.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

            // Provider Info
            Column {
                Text(
                    provider.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    provider.phoneNumber,
                    fontSize = 10.sp,
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
                        provider.rating.toString(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "₹ ${provider.fee}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        // Select Button
        Button(
            onClick = onSelect,
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) Color(0xFFE57373) else Color.White,
                contentColor = if (isSelected) Color.White else Color(0xFF66BB6A),
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color.Gray
            ),
            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)) else null,
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(28.dp)
        ) {
            Text(
                if (isSelected) "Unselect" else "Select",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Preview
@Composable
fun SalonClassicScreenPreview() {
    ServiceDetailScreen()
}

@Preview(showBackground = true)
@Composable
fun ServiceDetailsScreenContentPreview() {
    val sampleCategories = listOf(
        CategoryItem(id = "1", label = "Cleanup", image = ""),
        CategoryItem(id = "2", label = "Manicure", image = ""),
        CategoryItem(id = "3", label = "Pedicure", image = ""),
        CategoryItem(id = "4", label = "Facial", image = ""),
        CategoryItem(id = "5", label = "Bleach", image = ""),
    )

    val sampleSubServices = listOf(
        SubService(
            id = "101",
            title = "Classic Cleanup",
            rating = 4.8,
            reviewCount = 1860,
            durationMin = 30,
            price = 499,
            image = ""
        ),
        SubService(
            id = "102",
            title = "Fruit Cleanup",
            rating = 4.8,
            reviewCount = 1860,
            durationMin = 45,
            price = 699,
            image = ""
        )
    )

    val sampleSections = listOf(
        ServiceSection(
            id = "1",
            title = "Cleanup",
            items = sampleSubServices
        ),
        ServiceSection(
            id = "2",
            title = "Manicure",
            items = sampleSubServices.map { it.copy(id = it.id + "m") }
        )
    )

    val sampleServiceDetails = ServiceDetails(
        id = 1L,
        title = "Salon Classic",
        bannerTitle = "Salon Classic at home",
        bannerImage = "",
        rating = 4.8,
        reviewCount = 1860,
        bookingsText = "1.2M bookings in last 24 hours",
        categories = sampleCategories,
        sections = sampleSections
    )

    val uiState = ServiceDetailsUiState(
        isLoading = false,
        serviceDetails = sampleServiceDetails,
        selectedCategory = sampleCategories.first(),
        filteredSections = sampleSections,
        errorMessage = null
    )

    ServiceDetailsScreenContent(
        uiState = uiState,
        intent = {},
        snackbarHostState = remember { SnackbarHostState() }
    )
}
