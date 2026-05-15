package org.example.project.home.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.ic_location
import dukaankonnect.composeapp.generated.resources.ic_search
import dukaankonnect.composeapp.generated.resources.manv
import org.example.project.core.model.home.Banner
import org.example.project.core.model.home.Service
import org.example.project.core.network.dto.home.ServiceCategory
import org.example.project.home.presentation.viewmodels.HomeEffect
import org.example.project.home.presentation.viewmodels.HomeIntent
import org.example.project.home.presentation.viewmodels.HomeUiState
import org.example.project.home.presentation.viewmodels.HomeViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.example.project.core.utils.AddressFormatter

@Composable
fun HomeScreen(
    onServiceClick: (Int) -> Unit = {},
    onLocationClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToService -> {
                    onServiceClick(effect.id)
                }
                HomeEffect.OpenLocationPicker -> {
                    onLocationClick()
                }
                HomeEffect.OpenBanner -> { /* TODO open banner destination */ }
                is HomeEffect.ShowMessage -> {
                    val result = snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = "Retry",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.handleIntent(HomeIntent.Retry)
                    }
                }
            }
        }
    }

    val intent: (HomeIntent) -> Unit = viewModel::handleIntent
    HomeScreenContent(
        uiState = uiState,
        intent = intent,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    intent: (HomeIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { intent(HomeIntent.LocationClicked) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_location),
                            contentDescription = "Location",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AddressFormatter.formatShortAddress(uiState.userLocation ?: "No location set"),
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(horizontal = 4.dp)
                    ) {
                        TextField(
                            value = uiState.searchQuery,
                            onValueChange = { intent(HomeIntent.SearchQueryChanged(it)) },
                            enabled = uiState.isSearchEnabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White),
                            placeholder = { Text(if (uiState.isSearchEnabled) "Search for services and packages" else "Loading services...", fontSize = 14.sp) },
                            textStyle = TextStyle(fontSize = 14.sp),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_search),
                                    contentDescription = "Search",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color(0xFFF0F0F0), // Slight grey tint when disabled
                            )
                        )
                    }


                    AnimatedVisibility(
                        visible = uiState.searchQuery.isNotBlank(),
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF9F9F9))
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Search Results",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            if (uiState.searchResults.isNotEmpty()) {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(uiState.searchResults) { service ->
                                        ServiceItem(
                                            service = service,
                                            onClick = { intent(HomeIntent.ServiceClicked(service.id)) },
                                            modifier = Modifier.width(90.dp)
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "No services found for \"${uiState.searchQuery}\"",
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (uiState.searchQuery.isBlank()) {
                uiState.banner?.let { bannerData ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clickable(onClick = { intent(HomeIntent.BannerClicked) }),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF6C4DFF))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier.align(Alignment.CenterStart),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(16.dp),
                                    text = bannerData.title,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                Image(
                                    painter = painterResource(Res.drawable.manv),
                                    contentDescription = "Banner Image",
                                    modifier = Modifier
                                        .height(100.dp)
                                        .width(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                if (uiState.personalService.isNotEmpty()) {
                    ServiceSection(
                        title = "Personal Services",
                        services = uiState.personalService,
                        onServiceClick = { id -> intent(HomeIntent.ServiceClicked(id)) }
                    )
                }

                if (uiState.homeService.isNotEmpty()) {
                    ServiceSection(
                        title = "Home Services",
                        services = uiState.homeService,
                        onServiceClick = { id -> intent(HomeIntent.ServiceClicked(id)) }
                    )
                }

                if (uiState.trendingService.isNotEmpty()) {
                    ServiceSection(
                        title = "Trending Services",
                        services = uiState.trendingService,
                        onServiceClick = { id -> intent(HomeIntent.ServiceClicked(id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceSection(
    title: String,
    services: List<Service>,
    onServiceClick: (Int) -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp, start = 16.dp)
        )

        val itemSpacing = Arrangement.spacedBy(2.dp)

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = itemSpacing
        ) {
            items(services) { service ->
                ServiceItem(
                    service = service,
                    onClick = { onServiceClick(service.id) },
                    modifier = Modifier
                        .width(100.dp)
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ServiceItem(
    service: Service,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(75.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = service.icon,
                contentDescription = service.name,
                modifier = Modifier
                    .size(48.dp)
                    .padding(2.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = service.name,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp,
            modifier = Modifier.padding(horizontal = 3.5.dp)
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val dummyServices = listOf(
        Service(id = 1, name = "Electrical Plumbing", icon = "ic_electrical.xml", category = ServiceCategory.HOME),
        Service(id = 2, name = "Cleaning & Pest", icon = "ic_cleaning.xml", category = ServiceCategory.HOME),
        Service(id = 3, name = "Home repairs", icon = "ic_home_repairs.xml", category = ServiceCategory.HOME),
        Service(id = 4, name = "Home Painting", icon = "ic_painting.xml", category = ServiceCategory.HOME),
        Service(id = 5, name = "Salon for Women", icon = "ic_salon_women.xml", category = ServiceCategory.HOME)
    )

    val previewState = HomeUiState(
        isLoading = false,
        userLocation = "Kavuri Hills, Madhapur",
        banner = Banner(1, "Let’s make a package just for you, Manvi!", "", "", ""),
        personalService = dummyServices,
        homeService = dummyServices.take(4),
        trendingService = dummyServices.shuffled(),
        searchQuery = ""
    )

    HomeScreenContent(
        uiState = previewState,
        intent = {}
    )
}