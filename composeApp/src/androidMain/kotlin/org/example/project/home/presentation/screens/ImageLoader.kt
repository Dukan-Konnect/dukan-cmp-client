package org.example.project.home.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp

@Composable
actual fun asyncImageVectorOrPlaceholder(url: String): ImageVector {
    val context = LocalContext.current

    return when {
        url.startsWith("vector:") || url.startsWith("drawable/") -> {
            val resName = url.removePrefix("vector:").removePrefix("drawable/")
                .substringBeforeLast('.')
                .trim()
            val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)

            if (resId == 0) {
                // Fallback to placeholder
                val placeholderId = context.resources.getIdentifier("ic_placeholder", "drawable", context.packageName)
                if (placeholderId != 0) {
                    ImageVector.vectorResource(placeholderId)
                } else {
                    createEmptyImageVector()
                }
            } else {
                ImageVector.vectorResource(id = resId)
            }
        }

        else -> {
            // For any other case, return placeholder
            val placeholderId = context.resources.getIdentifier("ic_placeholder", "drawable", context.packageName)
            if (placeholderId != 0) {
                ImageVector.vectorResource(placeholderId)
            } else {
                createEmptyImageVector()
            }
        }
    }
}

private fun createEmptyImageVector(): ImageVector {
    return ImageVector.Builder(
        name = "empty",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).build()
}
