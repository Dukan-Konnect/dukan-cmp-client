package org.example.project.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp

/**
 * Android actual mapping for AppIcons using vector drawables from res/drawable.
 * Place your vector drawable files (XML) in composeApp/src/androidMain/res/drawable/
 */
actual object AppIcons {
    actual val location: ImageVector
        @Composable
        get() = loadVectorResource("ic_location")

    actual val search: ImageVector
        @Composable
        get() = loadVectorResource("ic_search")

    actual val placeholder: ImageVector
        @Composable
        get() = loadVectorResource("ic_placeholder")

    actual val arrowBack: ImageVector
        @Composable
        get() = loadVectorResource("ic_arrow_back")

    actual val close: ImageVector
        @Composable
        get() = loadVectorResource("ic_close")

    actual val signal: ImageVector
        @Composable
        get() = loadVectorResource("ic_signal")

    actual val battery: ImageVector
        @Composable
        get() = loadVectorResource("ic_battery")

    actual val wifi: ImageVector
        @Composable
        get() = loadVectorResource("ic_wifi")

    actual val bookmark: ImageVector
        @Composable
        get() = loadVectorResource("ic_bookmark")

    actual val arrowRight: ImageVector
        @Composable
        get() = loadVectorResource("ic_arrow_right")

    actual val star: ImageVector
        @Composable
        get() = loadVectorResource("ic_star")

    actual val menu: ImageVector
        @Composable
        get() = loadVectorResource("ic_menu")

    actual val locationPin: ImageVector
        @Composable
        get() = loadVectorResource("ic_location_pin")

    actual val myLocation: ImageVector
        @Composable
        get() = loadVectorResource("ic_my_location")

    actual val plus: ImageVector
        @Composable
        get() = loadVectorResource("ic_plus")

    actual val coupon: ImageVector
        @Composable
        get() = loadVectorResource("ic_coupon")

    actual val home: ImageVector
        @Composable
        get() = loadVectorResource("ic_home")

    actual val edit: ImageVector
        @Composable
        get() = loadVectorResource("ic_edit")

    actual val calendarClock: ImageVector
        @Composable
        get() = loadVectorResource("ic_calendar_clock")

    actual val add: ImageVector
        @Composable
        get() = loadVectorResource("ic_add")

    actual val moreVert: ImageVector
        @Composable
        get() = loadVectorResource("ic_more_vert")

    actual val delete: ImageVector
        @Composable
        get() = loadVectorResource("ic_delete")

    actual val info: ImageVector
        @Composable
        get() = loadVectorResource("ic_info")

    actual val heartOutline: ImageVector
        @Composable
        get() = loadVectorResource("ic_heart_outline")

    actual val calendar: ImageVector
        @Composable
        get() = loadVectorResource("ic_calendar")

    actual val personLarge: ImageVector
        @Composable
        get() = loadVectorResource("ic_person_large")

    actual val camera: ImageVector
        @Composable
        get() = loadVectorResource("ic_camera")

    @Composable
    actual fun serviceIcon(name: String): ImageVector {
        // Normalize the service icon name (remove path and extension)
        val iconName = name
            .substringAfterLast('/')
            .substringBeforeLast('.')
            .trim()

        return loadVectorResource(iconName)
    }
}

/**
 * Load an ImageVector from Android drawable resources by name.
 * Falls back to ic_placeholder if the resource is not found.
 */
@Composable
private fun loadVectorResource(name: String): ImageVector {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(name, "drawable", context.packageName)

    return if (resId != 0) {
        ImageVector.vectorResource(resId)
    } else {
        // Try fallback to ic_placeholder
        val fallbackId = context.resources.getIdentifier("ic_placeholder", "drawable", context.packageName)
        if (fallbackId != 0) {
            ImageVector.vectorResource(fallbackId)
        } else {
            // If even placeholder is missing, create a simple empty vector
            ImageVector.Builder(
                name = "empty",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).build()
        }
    }
}
