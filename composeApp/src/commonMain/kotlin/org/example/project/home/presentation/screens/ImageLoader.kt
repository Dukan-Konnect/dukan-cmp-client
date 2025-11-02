package org.example.project.home.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
expect fun asyncImageVectorOrPlaceholder(url: String): ImageVector
