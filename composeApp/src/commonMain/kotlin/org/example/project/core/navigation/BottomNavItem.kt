package org.example.project.core.navigation

import androidx.compose.runtime.Immutable
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.ic_bookmark
import dukaankonnect.composeapp.generated.resources.ic_home
import dukaankonnect.composeapp.generated.resources.ic_person_large
import org.jetbrains.compose.resources.DrawableResource

@Immutable
sealed class BottomNavItem(val route: Any, val icon: DrawableResource, val label: String) {
    object Home : BottomNavItem(HomeRoute, Res.drawable.ic_home, "Home")
    object Bookings : BottomNavItem(BookingsRoute(), Res.drawable.ic_bookmark, "Bookings")
    object Profile : BottomNavItem(ProfileRoute, Res.drawable.ic_person_large, "Profile")

    companion object {
        val items = listOf(Home, Bookings, Profile)
    }
}
