package org.example.project.onboarding.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.location_not_available
import kotlinx.coroutines.delay
import org.example.project.onboarding.presentation.viewmodel.*
import org.example.project.ui.LottieAnimation
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun LocationFetchScreenWithPermissions(
    onLocationFetched: () -> Unit
)

@Composable
fun LocationFetchScreen(
    onLocationFetched: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onPromptGpsSettings: () -> Unit,
    viewModel: LocationFetchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LocationFetchEffect.NavigateToNextScreen -> {
                    if (uiState.isCompleted) delay(1500)
                    onLocationFetched()
                }
                is LocationFetchEffect.OpenAppSettings -> onOpenAppSettings()
                is LocationFetchEffect.PromptGpsSettings -> onPromptGpsSettings()
            }
        }
    }

    LocationFetchContent(
        uiState = uiState,
        onIntent = { viewModel.handleIntent(it) }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LocationFetchContent(
    uiState: LocationFetchUiState,
    onIntent: (LocationFetchIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
            }
        ) { step ->
            when (step) {
                LocationFetchStep.FETCHING -> LocationFetchingAnimation()
                LocationFetchStep.COMPLETED -> LocationCompletedAnimation(uiState.address)
                LocationFetchStep.ERROR -> {
                    // IMAGE PLACEHOLDER FOR ERRORS
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(72.dp),
                            painter = painterResource(Res.drawable.location_not_available),
                            contentDescription = "",
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Titles
        if (uiState.currentStep != LocationFetchStep.ERROR) {
            Text(
                text = when (uiState.currentStep) {
                    LocationFetchStep.FETCHING -> "Fetching your location..."
                    LocationFetchStep.COMPLETED -> "Location fetched successfully!"
                    else -> ""
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Subtitles and Error Controls
        if (uiState.currentStep == LocationFetchStep.COMPLETED && uiState.address != null) {
            Text(
                text = uiState.address,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        } else if (uiState.currentStep == LocationFetchStep.ERROR && uiState.errorState != null) {

            // Error Message
            Text(
                text = uiState.errorState.message,
                fontSize = 15.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Action Button (Settings / Turn On / Retry)
            Button(
                onClick = { onIntent(LocationFetchIntent.ActionClicked) },
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6CF7)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = uiState.errorState.primaryButtonText,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            // Secondary Retry Button (Only for GPS_DISABLED, allows manual retry after pulling status bar)
            if (uiState.errorState.showSecondaryRetry) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onIntent(LocationFetchIntent.RetryClicked) },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("I've turned it on, Retry", color = Color(0xFF4A6CF7))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Without Location Hyperlink
            Text(
                text = "Continue without location",
                color = Color(0xFF4A6CF7),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { onIntent(LocationFetchIntent.ContinueWithoutLocation) }
                    .padding(8.dp)
            )
        }
    }
}

// ... Keep your LocationFetchingAnimation and LocationCompletedAnimation here
@Composable
private fun LocationFetchingAnimation() {
    LottieAnimation(
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
    LottieAnimation(
        animationUrl = "https://lottie.host/17c7befc-f9f2-4e6e-a977-b7c9f5455f17/2HFHUsSkt1.lottie",
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(1f),
        loop = false,
        speed = 1f
    )
}