package org.example.project.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp

/**
 * Android-specific actual implementation. Tries to load a drawable resource by name.
 * Expected input examples:
 *  - "drawable/ic_search.xml"
 *  - "drawable/ic_electrical.xml"
 *  - "ic_search" or "ic_search.xml"
 *
 * Behavior:
 * 1. Try to resolve the requested resource name from the drawable folder.
 * 2. If not found, try to resolve a fallback drawable named `ic_placeholder`.
 * 3. If neither is present, return an empty ImageVector fallback.
 *
 * Add an `ic_placeholder` drawable to your Android project's drawable resources to
 * ensure a drawable is always used as a fallback.
 */
@Composable
actual fun asyncImageVectorOrPlaceholder(resourcePath: String): ImageVector {
    val context = LocalContext.current

    // Normalize resource name: accept "drawable/name.ext", "drawable/name", or just "name.ext"/"name"
    val requestedName = resourcePath
        .substringAfterLast('/') // removes "drawable/" if present
        .substringBeforeLast('.') // removes extension if present
        .trim()

    // Resolve requested resource id (cached across recompositions)
    val requestedResId = remember(requestedName) {
        if (requestedName.isBlank()) 0 else context.resources.getIdentifier(requestedName, "drawable", context.packageName)
    }

    if (requestedResId != 0) {
        return ImageVector.vectorResource(id = requestedResId)
    }

    // Try fallback drawable named `ic_placeholder`
    val fallbackName = "ic_placeholder"
    val fallbackResId = remember(fallbackName) { context.resources.getIdentifier(fallbackName, "drawable", context.packageName) }

    if (fallbackResId != 0) {
        return ImageVector.vectorResource(id = fallbackResId)
    }

    // Final fallback: empty ImageVector
    return ImageVector.Builder(
        name = "empty",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).build()
}
