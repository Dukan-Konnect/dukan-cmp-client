package org.example.project.core.resources

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * iOS actual mapping for AppIcons using Material Icons (for now).
 * Later you can replace with iOS-specific vector resources.
 */
actual object AppIcons {
    actual val location: ImageVector
        @Composable
        get() = Icons.Default.LocationOn

    actual val search: ImageVector
        @Composable
        get() = Icons.Default.Search

    actual val placeholder: ImageVector
        @Composable
        get() = Icons.Default.Info

    actual val arrowBack: ImageVector
        @Composable
        get() = Icons.Default.ArrowBack

    actual val close: ImageVector
        @Composable
        get() = Icons.Default.Close

    actual val signal: ImageVector
        @Composable
        get() = Icons.Default.Info

    actual val battery: ImageVector
        @Composable
        get() = Icons.Default.Info

    actual val wifi: ImageVector
        @Composable
        get() = Icons.Default.Info

    actual val bookmark: ImageVector
        @Composable
        get() = Icons.Default.BookmarkBorder

    actual val arrowRight: ImageVector
        @Composable
        get() = Icons.Default.ArrowForward

    actual val star: ImageVector
        @Composable
        get() = Icons.Default.Star

    actual val menu: ImageVector
        @Composable
        get() = Icons.Default.Menu

    actual val locationPin: ImageVector
        @Composable
        get() = Icons.Default.LocationOn

    actual val myLocation: ImageVector
        @Composable
        get() = Icons.Default.MyLocation

    actual val plus: ImageVector
        @Composable
        get() = Icons.Default.Add

    actual val coupon: ImageVector
        @Composable
        get() = Icons.Default.Info

    actual val home: ImageVector
        @Composable
        get() = Icons.Default.Home

    actual val edit: ImageVector
        @Composable
        get() = Icons.Default.Edit

    actual val calendarClock: ImageVector
        @Composable
        get() = Icons.Default.DateRange

    actual val add: ImageVector
        @Composable
        get() = Icons.Default.Add

    actual val moreVert: ImageVector
        @Composable
        get() = Icons.Default.MoreVert

    actual val delete: ImageVector
        @Composable
        get() = Icons.Default.Delete

    actual val info: ImageVector
        @Composable
        get() = Icons.Default.Info

    actual val heartOutline: ImageVector
        @Composable
        get() = Icons.Default.FavoriteBorder

    actual val calendar: ImageVector
        @Composable
        get() = Icons.Default.DateRange

    actual val personLarge: ImageVector
        @Composable
        get() = Icons.Default.Person

    actual val camera: ImageVector
        @Composable
        get() = Icons.Default.CameraAlt

    actual val arrowDown: ImageVector
        @Composable
        get() = Icons.Default.KeyboardArrowDown

    actual val arrowUp: ImageVector
        @Composable
        get() = Icons.Default.KeyboardArrowUp

    @Composable
    actual fun serviceIcon(name: String): ImageVector {
        return when {
            name.contains("salon", ignoreCase = true) -> Icons.Default.Home
            name.contains("cleaning", ignoreCase = true) -> Icons.Default.Home
            name.contains("electrical", ignoreCase = true) -> Icons.Default.Home
            name.contains("painting", ignoreCase = true) -> Icons.Default.Home
            name.contains("repair", ignoreCase = true) -> Icons.Default.Home
            else -> placeholder
        }
    }
}
