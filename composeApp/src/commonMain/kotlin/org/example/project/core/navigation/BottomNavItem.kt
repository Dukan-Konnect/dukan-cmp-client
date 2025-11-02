package org.example.project.core.navigation

import androidx.compose.runtime.Immutable

@Immutable
sealed class BottomNavItem(val route: Any, val icon: String, val label: String) {
    object Home : BottomNavItem(HomeRoute, "ic_home", "Home")
    object Bookings : BottomNavItem(BookingsRoute, "ic_bookings", "Bookings")
    object Profile : BottomNavItem(ProfileRoute, "ic_profile", "Profile")

    companion object {
        val items = listOf(Home, Bookings, Profile)
    }
}
