package org.example.project.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.example.project.core.navigation.BookingsRoute
import org.example.project.core.navigation.HomeRoute
import org.example.project.core.navigation.ProfileRoute
import org.example.project.home.presentation.screens.BookingsScreen
import org.example.project.home.presentation.screens.HomeScreen
import org.example.project.home.presentation.screens.ServiceDetailScreen
import org.example.project.home.presentation.screens.SummaryScreen
import org.example.project.onboarding.presentation.navigation.LocationFetchRoute
import org.example.project.onboarding.presentation.navigation.navigateToLocationFetchScreen
import org.example.project.profile.presentation.screens.EditProfileScreen
import org.example.project.profile.presentation.screens.ProfileScreen


fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) = navigate(HomeRoute, navOptions)
fun NavController.navigateToBookingsScreen(navOptions: NavOptions? = null) = navigate(BookingsRoute, navOptions)
fun NavController.navigateToProfileScreen(navOptions: NavOptions? = null) = navigate(ProfileRoute, navOptions)
fun NavController.navigateToServiceDetailScreen(serviceId: Long) = navigate(ServiceDetailRoute(serviceId))
fun NavController.navigateToSummaryScreen() = navigate(SummaryRoute)
fun NavController.navigateToEditProfileScreen() = navigate(EditProfileRoute)

fun NavGraphBuilder.homeDestination(navController: NavController) {
    composable<HomeRoute> {
        HomeScreen(
            onServiceClick = { id ->
                navController.navigateToServiceDetailScreen(id.toLong())
            },
            onLocationClick = {
                navController.navigate(LocationFetchRoute) {
                    popUpTo<HomeRoute> { inclusive = true }
                }
            }
        )
    }
}

fun NavGraphBuilder.bookingsDestination() {
    composable<BookingsRoute> {
        BookingsScreen()
    }
}

fun NavGraphBuilder.profileDestination(navController: NavController) {
    composable<ProfileRoute> {
        ProfileScreen(
            onEditProfileClick = navController::navigateToEditProfileScreen
        )
    }
}

fun NavGraphBuilder.editProfileDestination(navController: NavController) {
    composable<EditProfileRoute> {
        EditProfileScreen(
            onBack = navController::navigateUp
        )
    }
}

fun NavGraphBuilder.serviceDetailDestination(navController: NavController) {
    composable<ServiceDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ServiceDetailRoute>()
        ServiceDetailScreen(
            serviceId = route.serviceId,
            onNavigateToSummary = navController::navigateToSummaryScreen
        )
    }
}

fun NavGraphBuilder.summaryDestination(navController: NavController) {
    composable<SummaryRoute> {
        SummaryScreen(
            onBack = navController::navigateUp,
            onPay = {
                navController.popBackStack<HomeRoute>(inclusive = false)

                navController.navigate(BookingsRoute) {
                    popUpTo<HomeRoute> {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

// --- Main Graph Assembly ---
fun NavGraphBuilder.homeNavGraph(navController: NavController) {
    homeDestination(navController)
    bookingsDestination()
    profileDestination(navController)
    editProfileDestination(navController)
    serviceDetailDestination(navController)
    summaryDestination(navController)
}
