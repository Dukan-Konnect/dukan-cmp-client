package org.example.project.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Common declaration for platform-specific image vector provider.
 */
@Composable
expect fun asyncImageVectorOrPlaceholder(resourcePath: String): ImageVector
