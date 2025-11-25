package org.example.project.onboarding.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

        // Logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF4A6CF7), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "m",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Animation placeholder for location fetching
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
    // TODO: Replace with actual location animation library compatible with CMP
    // For now, using a simple loading animation
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = Color(0xFF4A6CF7),
            strokeWidth = 4.dp
        )

        // Pulsing circle animation placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF4A6CF7).copy(alpha = 0.1f))
        )
    }
}

@Composable
private fun LocationCompletedAnimation(address: String?) {
    // TODO: Replace with actual completion animation
    // For now, using a simple checkmark placeholder
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color(0xFF4CAF50)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✓",
            fontSize = 48.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
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
