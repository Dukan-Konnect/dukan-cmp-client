package org.example.project.home.presentation.screens

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
import org.example.project.core.log
import org.example.project.home.domain.model.Banner
import org.example.project.home.domain.model.Service
import org.example.project.home.domain.model.ServiceCategory
import org.example.project.home.domain.model.UserLocation
import org.example.project.home.presentation.viewmodels.HomeEffect
import org.example.project.home.presentation.viewmodels.HomeEvent
import org.example.project.home.presentation.viewmodels.HomeUiState
import org.example.project.home.presentation.viewmodels.HomeViewModel
import org.example.project.core.resources.AppIcons
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.example.project.home.utils.AddressFormatter

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onBookingClick: () -> Unit = {},
    onServiceClick: (Int) -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {

    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-off effects
    LaunchedEffect(viewModel) {
        log("navigatu","LaunchedEffect: HomeViewModel")
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToService -> {
                    onServiceClick(effect.id)
                    log("servicedetails","id = ${effect.id}")
                }
                HomeEffect.OpenLocationPicker -> { /* TODO open picker */ }
                HomeEffect.OpenBanner -> { /* TODO open banner destination */ }
                is HomeEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    // Forward intents directly to the ViewModel reducer
    val intent: (HomeEvent) -> Unit = viewModel::onEvent

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
    intent: (HomeEvent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        // Loading
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Error state snackbar inline (state-driven)
        uiState.errorMessage?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { intent(HomeEvent.Retry) }) { Text("Retry") }
                },
                dismissAction = {
                    TextButton(onClick = { intent(HomeEvent.ErrorDismissed) }) { Text("Dismiss") }
                }
            ) { Text(error) }
        }

        // Optional host for effect-based messages
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
            // Top Bar with Status Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column {
                    // Location Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { intent(HomeEvent.LocationClicked) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.location,
                            contentDescription = "Location",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            AddressFormatter.formatShortAddress(uiState.savedAddress ?: uiState.userLocation?.address),
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Search Bar: editable TextField bound to uiState
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(horizontal = 4.dp)

                    ) {
                        TextField(
                            value = uiState.searchQuery,
                            onValueChange = { intent(HomeEvent.SearchQueryChanged(it)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White),
                            placeholder = { Text("Search for services and packages", fontSize = 14.sp) },
                            textStyle = TextStyle(fontSize = 14.sp),
                            leadingIcon = {
                                Icon(
                                    imageVector = AppIcons.search,
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
                                disabledContainerColor = Color.White,
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Banner Card
            uiState.banner?.let { bannerData ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { intent(HomeEvent.BannerClicked) }),
                    // shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF6C4DFF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()

                    ) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterStart),
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
                            Spacer(modifier = Modifier.width(2.dp))

                            // Banner image placeholder (using Icon for now)
                            Icon(
                                imageVector = AppIcons.placeholder,
                                contentDescription = "Banner",
                                modifier = Modifier.size(120.dp),
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // Personal Services Section
            if (uiState.personalServices.isNotEmpty()) {
                ServiceSection(
                    title = "Personal Services",
                    services = uiState.personalServices,
                    onServiceClick = { id -> intent(HomeEvent.ServiceClicked(id)) }
                )

            }

            // Home Services Section
            if (uiState.homeServices.isNotEmpty()) {
                ServiceSection(
                    title = "Home Services",
                    services = uiState.homeServices,
                    onServiceClick = { id -> intent(HomeEvent.ServiceClicked(id)) }
                )

            }

            // Trending Services Section
            if (uiState.trendingServices.isNotEmpty()) {
                ServiceSection(
                    title = "Trending Services",
                    services = uiState.trendingServices,
                    onServiceClick = { id -> intent(HomeEvent.ServiceClicked(id)) }
                )

            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
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
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp,start = 16.dp)
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
                        .width(96.dp)
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ServiceItem(
    service: Service,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)

    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White), // white background to cover transparent icons
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = service.icon,
                contentDescription = service.name,
                modifier = Modifier
                    .size(40.dp)
                    .padding(2.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            service.name,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            lineHeight = 14.sp,
            modifier = Modifier.padding(horizontal = 3.5.dp)
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Preview
@Composable
fun HomeScreenPreview() {
    val dummyServices = listOf(
        Service(id = 1, name = "Electrical Plumbing", icon = "ic_electrical.xml", ServiceCategory.HOME),
        Service(id = 2, name = "Cleaning & Pest", icon = "ic_cleaning.xml",ServiceCategory.HOME),
        Service(id = 3, name = "Home repairs", icon = "ic_home_repairs.xml",ServiceCategory.HOME),
        Service(id = 4, name = "Home Painting", icon = "ic_painting.xml",ServiceCategory.HOME),
        Service(id = 5, name = "Salon for Women", icon = "ic_salon_women.xml",ServiceCategory.HOME)
    )

    val previewState = HomeUiState(
        isLoading = false,
        userLocation = UserLocation("Kavuri Hills, Madhapur"),
        banner = Banner(1, "Let’s make a package just for you, Manvi!", "","",""),
        personalServices = dummyServices,
        homeServices = dummyServices.take(4),
        trendingServices = dummyServices.shuffled(),
        searchQuery = ""
    )

    HomeScreenContent(
        uiState = previewState,
        intent = {}
    )
}
