package org.example.project.onboarding.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import org.example.project.core.navigation.HomeRoute
import org.example.project.core.navigation.OnboardingRoute
import org.example.project.onboarding.presentation.screens.LocationFetchScreenWithPermissions
import org.example.project.onboarding.presentation.screens.LoginScreen
import org.example.project.onboarding.presentation.screens.NameCaptureScreen
import org.example.project.onboarding.presentation.screens.OTPScreen
import org.example.project.onboarding.presentation.screens.OnboardingScreen
import org.example.project.onboarding.presentation.viewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.serialization.Serializable

// --- Routes ---

fun NavController.navigateToAuthGraph() {
    navigate(AuthRoute) {
        popUpTo<OnboardingRoute> { inclusive = true }
    }
}

fun NavController.navigateToOtpScreen() = navigate(OtpRoute)

fun NavController.navigateToNameCaptureScreen(phoneNumber: String) {
    navigate(NameCaptureRoute(phoneNumber)) {
        popUpTo<AuthRoute> { inclusive = true }
    }
}

fun NavController.navigateToLocationFetchScreen() = navigate(LocationFetchRoute)

// --- Destination Builders ---
fun NavGraphBuilder.onboardingDestination(navController: NavController) {
    composable<OnboardingRoute> {
        var currentPage by remember { mutableIntStateOf(0) }
        OnboardingScreen(
            currentPage = currentPage,
            onNextClick = {
                if (currentPage < 2) {
                    currentPage++
                } else {
                    navController.navigateToAuthGraph()
                }
            },
            onSkipClick = navController::navigateToAuthGraph
        )
    }
}

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation<AuthRoute>(startDestination = LoginRoute) {

        composable<LoginRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<AuthRoute>()
            }
            val sharedViewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            LoginScreen(
                viewModel = sharedViewModel,
                onNavigateToOtp = navController::navigateToOtpScreen
            )
        }

        composable<OtpRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<AuthRoute>()
            }
            val sharedViewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            OTPScreen(
                viewModel = sharedViewModel,
                onAuthSuccess = { phoneNumber ->
                    navController.navigateToNameCaptureScreen(phoneNumber)
                }
            )
        }
    }
}

fun NavGraphBuilder.nameCaptureDestination() {
    composable<NameCaptureRoute> {
        NameCaptureScreen()
    }
}

fun NavGraphBuilder.locationFetchDestination(navController: NavController) {
    composable<LocationFetchRoute> {
        LocationFetchScreenWithPermissions(
            onLocationFetched = {
                navController.navigate(HomeRoute) {
                    popUpTo<LocationFetchRoute> { inclusive = true }
                }
            }
        )
    }
}

// --- Main Graph Assembly ---
fun NavGraphBuilder.onboardingNavGraph(navController: NavController) {
    onboardingDestination(navController)
    authGraph(navController)
    nameCaptureDestination()
    locationFetchDestination(navController)
}