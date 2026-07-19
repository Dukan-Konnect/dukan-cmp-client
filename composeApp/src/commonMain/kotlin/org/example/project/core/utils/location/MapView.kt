package org.example.project.core.utils.location

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapView(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    onCameraPositionChanged: (Double, Double) -> Unit = { _, _ -> }
)
