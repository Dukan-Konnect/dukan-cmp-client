package org.example.project.onboarding.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import org.example.project.core.navigation.HomeRoute
import org.example.project.core.navigation.OnboardingRoute
import org.example.project.onboarding.presentation.screens.LoginScreen
import org.example.project.onboarding.presentation.screens.LocationFetchScreenWithPermissions
import org.example.project.onboarding.presentation.screens.NameCaptureScreen
import org.example.project.onboarding.presentation.screens.OTPScreen
import org.example.project.onboarding.presentation.screens.OnboardingScreen
import org.example.project.onboarding.presentation.viewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel


fun NavGraphBuilder.onboardingNavGraph(navController: NavHostController) {
    composable<OnboardingRoute> {
        var currentPage by remember { mutableStateOf(0) }
        OnboardingScreen(
            currentPage = currentPage,
            onNextClick = {
                if (currentPage < 2) {
                    currentPage++
                } else {
                    navController.navigate(AuthRoute) {
                        popUpTo<OnboardingRoute> { inclusive = true }
                    }
                }
            },
            onSkipClick = {
                navController.navigate(AuthRoute) {
                    popUpTo<OnboardingRoute> { inclusive = true }
                }
            }
        )
    }
    navigation<AuthRoute>(startDestination = LoginRoute) {

        composable<LoginRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<AuthRoute>()
            }
            val sharedViewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            LoginScreen(
                viewModel = sharedViewModel,
                onNavigateToOtp = {
                    navController.navigate(OtpRoute)
                }
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
                    navController.navigate(NameCaptureRoute(phoneNumber)) {
                        // Pop the entire AuthRoute graph so user can't go back to OTP
                        popUpTo<AuthRoute> { inclusive = true }
                    }
                }
            )
        }
    }

    composable<NameCaptureRoute> { backStackEntry ->
        val args = backStackEntry.toRoute<NameCaptureRoute>()
        NameCaptureScreen(
            phoneNumber = args.phoneNumber,
            onNameConfirmed = {
                navController.navigate(LocationFetchRoute) {
                    popUpTo<NameCaptureRoute> { inclusive = true }
                }
            }
        )
    }

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
