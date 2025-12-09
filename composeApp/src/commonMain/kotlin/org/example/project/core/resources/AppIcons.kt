package org.example.project.core.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Provide ImageVector icons from common code. Implement platform-specific mappings in `actual`.
 */
expect object AppIcons {
    val location: ImageVector
        @Composable get

    val search: ImageVector
        @Composable get

    val placeholder: ImageVector
        @Composable get

    val arrowBack: ImageVector
        @Composable get

    val close: ImageVector
        @Composable get

    val signal: ImageVector
        @Composable get

    val battery: ImageVector
        @Composable get

    val wifi: ImageVector
        @Composable get

    val bookmark: ImageVector
        @Composable get

    val arrowRight: ImageVector
        @Composable get

    val star: ImageVector
        @Composable get

    val menu: ImageVector
        @Composable get

    val locationPin: ImageVector
        @Composable get

    val myLocation: ImageVector
        @Composable get

    val plus: ImageVector
        @Composable get

    val coupon: ImageVector
        @Composable get

    val home: ImageVector
        @Composable get

    val edit: ImageVector
        @Composable get

    val calendarClock: ImageVector
        @Composable get

    val add: ImageVector
        @Composable get

    val moreVert: ImageVector
        @Composable get

    val delete: ImageVector
        @Composable get

    val info: ImageVector
        @Composable get

    val heartOutline: ImageVector
        @Composable get

    val calendar: ImageVector
        @Composable get

    val personLarge: ImageVector
        @Composable get

    val camera: ImageVector
        @Composable get

    val arrowDown: ImageVector
        @Composable get

    val arrowUp: ImageVector
        @Composable get

    val arrowForward: ImageVector
        @Composable get

    val phone: ImageVector
        @Composable get

    val address: ImageVector
        @Composable get

    val settings: ImageVector
        @Composable get

    val logout: ImageVector
        @Composable get

    val share: ImageVector
        @Composable get

    val back: ImageVector
        @Composable get

    /**
     * Convert a service-provided icon string (e.g. "ic_salon_women.xml") to a platform ImageVector.
     */
    @Composable
    fun serviceIcon(name: String): ImageVector
}
