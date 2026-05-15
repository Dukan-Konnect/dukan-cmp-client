package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.Url
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
fun LottieAnimation(
    animationUrl: String,
    modifier: Modifier = Modifier,
    loop: Boolean = true,
    speed: Float = 1f
) {
    // Fetch and parse the Lottie file from the internet
//    val composition by rememberLottieComposition {
//        LottieCompositionSpec.Url(animationUrl)
//    }
//
//    // Create the painter that handles the frame-by-frame drawing
//    val painter = rememberLottiePainter(
//        composition = composition,
//        iterations = if (loop) Compottie.IterateForever else 1,
//        speed = speed
//    )
//
//    // Draw it to the canvas
//    Image(
//        painter = painter,
//        contentDescription = "Lottie Animation",
//        modifier = modifier
//    )
}