package org.example.project.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

// Android actual implementation using Material Icons
actual object PlatformIcons {
    actual val Home: ImageVector = Icons.Default.Home
    actual val Bookings: ImageVector = Icons.Default.DateRange
    actual val Profile: ImageVector = Icons.Default.AccountCircle
}
