package org.example.project.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.ic_arrow_back
import dukaankonnect.composeapp.generated.resources.ic_arrow_down
import dukaankonnect.composeapp.generated.resources.ic_arrow_up
import dukaankonnect.composeapp.generated.resources.ic_calendar
import dukaankonnect.composeapp.generated.resources.ic_edit
import dukaankonnect.composeapp.generated.resources.ic_star
import org.example.project.home.domain.model.CategoryItem
import org.example.project.home.domain.model.ServiceDetails
import org.example.project.home.domain.model.ServiceProvider
import org.example.project.home.domain.model.SubService
import org.example.project.home.presentation.navigation.SummaryRoute
import org.example.project.home.presentation.viewmodels.ServiceDetailsEffect
import org.example.project.home.presentation.viewmodels.ServiceDetailsEvent
import org.example.project.home.presentation.viewmodels.ServiceDetailsUiState
import org.example.project.home.presentation.viewmodels.ServiceDetailsViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.flow.collect
import org.example.project.core.presentation.GenericErrorScreen

@Composable
fun ServiceDetailScreen(
    onBackClick: () -> Unit = {},
    onSubServiceClick: (String) -> Unit = {},
    onNavigateToSummary: (SummaryRoute) -> Unit = {},
    viewModel: ServiceDetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ServiceDetailsEffect.NavigateBack -> onBackClick()
                is ServiceDetailsEffect.NavigateToSubServiceDetails -> onSubServiceClick(effect.subServiceId)
                is ServiceDetailsEffect.NavigateToSummary -> onNavigateToSummary(effect.route)
                is ServiceDetailsEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    ServiceDetailsScreenContent(
        uiState = uiState,
        intent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ServiceDetailsScreenContent(
    uiState: ServiceDetailsUiState,
    intent: (ServiceDetailsEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val expandedSections = remember { mutableStateMapOf<String, Boolean>() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            uiState.errorMessage?.let { error ->
                GenericErrorScreen(
                    title = "Failed to load service details",
                    message = error,
                    onRetry = { intent(ServiceDetailsEvent.Retry) },
                    onLogout = { intent(ServiceDetailsEvent.Logout) },
                )
            }

            val serviceDetails = uiState.serviceDetails ?: return@Column

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
                    IconButton(onClick = { intent(ServiceDetailsEvent.BackClicked) }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp)
            ) {
                ServiceHeader(serviceDetails)

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
                                    CategoryCard(
                                        category = category,
                                        onClick = { expandedSections[category.id] = true }
                                    )
                                }
                            }
                        }
                    }
                }

                uiState.filteredSections.forEach { section ->
                    ExpandableServiceSection(
                        title = section.title,
                        expanded = expandedSections[section.id] ?: false,
                        onExpandChange = { expandedSections[section.id] = it }
                    ) {
                        section.items.forEach { subService ->
                            val selectedProvider = if (uiState.selectedSubServiceId == subService.id) {
                                uiState.selectedProvider
                            } else {
                                null
                            }

                            SubServiceItem(
                                subService = subService,
                                selectedProvider = selectedProvider,
                                availableProviders = uiState.availableProviders[subService.id],
                                isExpanded = uiState.expandedSubservices.contains(subService.id),
                                onToggleDropdown = {
                                    intent(ServiceDetailsEvent.ToggleProviderDropdown(subService.id))
                                },
                                onSelectProvider = { provider ->
                                    intent(ServiceDetailsEvent.SelectProvider(subService.id, provider))
                                },
                                onClick = {
                                    intent(ServiceDetailsEvent.SubServiceClicked(subService.id))
                                }
                            )
                        }
                    }
                }
            }
        }

        if (uiState.hasSelection) {
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
                            text = "₹ ${uiState.selectedProviderFeeCents / 100}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Text(
                            text = uiState.selectedProvider?.name.orEmpty(),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Button(
                        onClick = { intent(ServiceDetailsEvent.BookNowClicked) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Continue", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (uiState.hasSelection) 72.dp else 16.dp)
        )
    }
}

@Composable
private fun ServiceHeader(serviceDetails: ServiceDetails) {
    Column {
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

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    serviceDetails.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_star),
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
                        painter = painterResource(Res.drawable.ic_calendar),
                        contentDescription = "Bookings",
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
            }
        }
    }
}

@Composable
private fun CategoryCard(
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
                    painter = painterResource(Res.drawable.ic_edit),
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
            lineHeight = 14.sp
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
                    painter = if (expanded) {
                        painterResource(Res.drawable.ic_arrow_up)
                    } else {
                        painterResource(Res.drawable.ic_arrow_down)
                    },
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color.Gray
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
private fun SubServiceItem(
    subService: SubService,
    selectedProvider: ServiceProvider?,
    availableProviders: List<ServiceProvider>?,
    isExpanded: Boolean,
    onToggleDropdown: () -> Unit,
    onSelectProvider: (ServiceProvider) -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
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
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = subService.title,
                        modifier = Modifier.size(25.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subService.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subService.ratingText,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Starts at ₹ ${subService.price}",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleDropdown),
            color = Color(0xFFF9F7FF),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedProvider?.name ?: "Choose a provider",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Icon(
                        painter = if (isExpanded) {
                            painterResource(Res.drawable.ic_arrow_up)
                        } else {
                            painterResource(Res.drawable.ic_arrow_down)
                        },
                        contentDescription = "Provider options",
                        tint = Color.Gray
                    )
                }

                selectedProvider?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹ ${it.fee}",
                        fontSize = 12.sp,
                        color = Color(0xFF6C4DFF)
                    )
                }

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    availableProviders.orEmpty().forEach { provider ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clickable { onSelectProvider(provider) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedProvider?.id == provider.id) {
                                Color(0xFFECE7FF)
                            } else {
                                Color.White
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = provider.imageUrl,
                                    contentDescription = provider.name,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        provider.name,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                    Text(
                                        "₹ ${provider.fee}  •  ${provider.rating}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
