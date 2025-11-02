package org.example.project.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.example.project.home.presentation.screens.HomeScreen
import org.example.project.bookings.presentation.screens.BookingsScreen
import org.example.project.home.presentation.navigation.homeNavGraph
import org.example.project.onboarding.presentation.navigation.onboardingNavGraph
import org.example.project.profile.presentation.screens.ProfileScreen

@Composable
fun AppNavGraph(
    startDestination: Any
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom bar only for main screens
            if (shouldShowBottomBar(currentRoute)) {
                BottomBar(
                    currentRoute = getCurrentRouteObject(currentRoute),
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to the start destination and save state
                            popUpTo(HomeRoute) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            onboardingNavGraph(navController)
            homeNavGraph(navController)
        }
    }
}

private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when {
        currentRoute?.contains("HomeRoute") == true -> true
        currentRoute?.contains("BookingsRoute") == true -> true
        currentRoute?.contains("ProfileRoute") == true -> true
        else -> false
    }
}

private fun getCurrentRouteObject(currentRoute: String?): Any? {
    return when {
        currentRoute?.contains("HomeRoute") == true -> HomeRoute
        currentRoute?.contains("BookingsRoute") == true -> BookingsRoute
        currentRoute?.contains("ProfileRoute") == true -> ProfileRoute
        else -> null
    }
}
