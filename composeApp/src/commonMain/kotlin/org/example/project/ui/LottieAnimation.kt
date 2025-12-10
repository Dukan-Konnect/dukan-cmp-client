package org.example.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific Lottie animation composable.
 * On Android: Uses lottie-compose library
 * On other platforms: Uses a fallback placeholder
 */
@Composable
expect fun LottieAnimation(
    animationUrl: String,
    modifier: Modifier = Modifier,
    loop: Boolean = true,
    speed: Float = 1f
)

