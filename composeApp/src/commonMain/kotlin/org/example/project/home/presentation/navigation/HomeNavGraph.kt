package org.example.project.home.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.example.project.core.navigation.BookingsRoute
import org.example.project.core.navigation.HomeRoute
import org.example.project.core.navigation.ProfileRoute
import org.example.project.home.presentation.screens.BookingsScreen
import org.example.project.home.presentation.screens.HomeScreen
import org.example.project.home.presentation.screens.ServiceDetailScreen
import org.example.project.home.presentation.screens.SummaryScreen
import org.example.project.profile.presentation.screens.ProfileScreen
import org.example.project.profile.presentation.screens.EditProfileScreen


fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    composable<HomeRoute> {
        HomeScreen(onServiceClick = { id ->
            navController.navigate(ServiceDetailRoute(id.toLong()))
        })
    }

    composable<BookingsRoute> {
        BookingsScreen()
    }

    composable<ProfileRoute> {
        ProfileScreen(
            onEditProfileClick = {
                navController.navigate(EditProfileRoute)
            }
        )
    }

    composable<EditProfileRoute> {
        EditProfileScreen(
            onBack = { navController.navigateUp() }
        )
    }

    composable<ServiceDetailRoute> { backStackEntry ->
         val route = backStackEntry.toRoute<ServiceDetailRoute>()
         ServiceDetailScreen(
             serviceId = route.serviceId,
             onNavigateToSummary = { navController.navigate(SummaryRoute) }
         )
    }

    composable<SummaryRoute> {
        SummaryScreen(
            onBack = { navController.navigateUp() }
        )
    }
}