package org.example.project.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*

/**
 * Android implementation of Lottie animation using lottie-compose library.
 * Supports both local raw resources and remote URLs.
 */
@Composable
actual fun LottieAnimation(
    animationUrl: String,
    modifier: Modifier,
    loop: Boolean,
    speed: Float
) {
    // For now, using placeholder URL - will be replaced with actual animation URLs
    // You can pass either:
    // - A URL like "https://assets.lottiefiles.com/packages/lf20_xxxxx.json"
    // - Or we could use raw resources if you add JSON files to res/raw/

    val composition by rememberLottieComposition(
        if (animationUrl.startsWith("http")) {
            LottieCompositionSpec.Url(animationUrl)
        } else {
            // If it's not a URL, treat it as a placeholder and show nothing
            LottieCompositionSpec.Url("") // Empty will just not load anything
        }
    )

    LottieAnimation(
        composition = composition,
        iterations = if (loop) LottieConstants.IterateForever else 1,
        modifier = modifier,
        speed = speed
    )
}

