package org.example.project.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.example.project.core.navigation.BookingsRoute
import org.example.project.core.navigation.EditAddressRoute
import org.example.project.core.navigation.HomeRoute
import org.example.project.core.navigation.ManageAddressRoute
import org.example.project.core.navigation.ProfileRoute
import org.example.project.home.presentation.screens.BookingsScreen
import org.example.project.home.presentation.screens.HomeScreen
import org.example.project.home.presentation.screens.ServiceDetailScreen
import org.example.project.home.presentation.screens.SummaryScreen
import org.example.project.onboarding.presentation.navigation.LocationFetchRoute
import org.example.project.onboarding.presentation.navigation.navigateToLocationFetchScreen
import org.example.project.profile.presentation.screens.EditAddressScreen
import org.example.project.profile.presentation.screens.EditProfileScreen
import org.example.project.profile.presentation.screens.ManageAddressScreen
import org.example.project.profile.presentation.screens.ProfileScreen


fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) = navigate(HomeRoute, navOptions)
fun NavController.navigateToBookingsScreen(navOptions: NavOptions? = null) = navigate(BookingsRoute, navOptions)
fun NavController.navigateToProfileScreen(navOptions: NavOptions? = null) = navigate(ProfileRoute, navOptions)
fun NavController.navigateToServiceDetailScreen(serviceId: Long) = navigate(ServiceDetailRoute(serviceId))
fun NavController.navigateToSummaryScreen(route: SummaryRoute) = navigate(route)
fun NavController.navigateToEditProfileScreen() = navigate(EditProfileRoute)
fun NavController.navigateToManageAddressScreen() = navigate(ManageAddressRoute)
fun NavController.navigateToEditAddressScreen(addressId: String = "") = navigate(EditAddressRoute(addressId))

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
            onEditProfileClick = navController::navigateToEditProfileScreen,
            onNavigateToManageAddress = navController::navigateToManageAddressScreen
        )
    }
}

fun NavGraphBuilder.manageAddressDestination(navController: NavController) {
    composable<ManageAddressRoute> {
        ManageAddressScreen(
            onBack = navController::navigateUp,
            onNavigateToAddAddress = { navController.navigateToEditAddressScreen() },
            onNavigateToEditAddress = navController::navigateToEditAddressScreen
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

fun NavGraphBuilder.editAddressDestination(navController: NavController) {
    composable<EditAddressRoute> {
        EditAddressScreen(
            onBack = navController::navigateUp
        )
    }
}

fun NavGraphBuilder.serviceDetailDestination(navController: NavController) {
    composable<ServiceDetailRoute> {
        ServiceDetailScreen(
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
    manageAddressDestination(navController)
    editProfileDestination(navController)
    editAddressDestination(navController)
    serviceDetailDestination(navController)
    summaryDestination(navController)
}
