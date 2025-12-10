package org.example.project.onboarding.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.onboarding.presentation.viewmodel.LocationFetchViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Platform-specific wrapper that handles permission requests before showing LocationFetchScreen.
 * Android implementation will request location permissions.
 * iOS implementation can handle location authorization.
 */
@Composable
expect fun LocationFetchScreenWithPermissions(
    onLocationFetched: () -> Unit
)

@Composable
fun LocationFetchScreen(
    onLocationFetched: () -> Unit,
    viewModel: LocationFetchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ViewModel will start its own initialization (e.g., fetch location) when created.
    LaunchedEffect(Unit) {
        viewModel.startLocationFlow()
    }

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            delay(1500) // Show completion animation for a bit
            onLocationFetched()
        }
    }

    LocationFetchContent(
        uiState = uiState,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LocationFetchContent(
    uiState: LocationFetchUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animation placeholder for location fetching - almost full width
        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
            }
        ) { step ->
            when (step) {
                LocationFetchStep.FETCHING -> {
                    LocationFetchingAnimation()
                }
                LocationFetchStep.COMPLETED -> {
                    LocationCompletedAnimation(uiState.address)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Status text
        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                fadeIn() with fadeOut()
            }
        ) { step ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (step) {
                        LocationFetchStep.FETCHING -> "Fetching your location..."
                        LocationFetchStep.COMPLETED -> "Location fetched successfully!"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                if (step == LocationFetchStep.COMPLETED && uiState.address != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.address,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.error,
                fontSize = 14.sp,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LocationFetchingAnimation() {
    // Lottie animation for location fetching - almost full width
    org.example.project.ui.LottieAnimation(
        animationUrl = "https://lottie.host/4c31b4f6-857d-419d-97e5-7c722e5c2e99/EhLlK9bqgJ.lottie",
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(1f),
        loop = true,
        speed = 1f
    )
}

@Composable
private fun LocationCompletedAnimation(address: String?) {
    // Lottie animation for completion - almost full width
    org.example.project.ui.LottieAnimation(
        animationUrl = "https://lottie.host/17c7befc-f9f2-4e6e-a977-b7c9f5455f17/2HFHUsSkt1.lottie",
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(1f),
        loop = false,
        speed = 1f
    )
}

enum class LocationFetchStep {
    FETCHING,
    COMPLETED
}

data class LocationFetchUiState(
    val currentStep: LocationFetchStep = LocationFetchStep.FETCHING,
    val address: String? = null,
    val isCompleted: Boolean = false,
    val error: String? = null
)
