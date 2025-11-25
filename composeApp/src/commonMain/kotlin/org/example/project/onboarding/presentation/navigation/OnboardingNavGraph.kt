package org.example.project.onboarding.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.example.project.core.navigation.HomeRoute
import org.example.project.core.navigation.OnboardingRoute
import org.example.project.onboarding.presentation.screens.LoginScreen
import org.example.project.onboarding.presentation.screens.LocationFetchScreenWithPermissions
import org.example.project.onboarding.presentation.screens.NameCaptureScreen
import org.example.project.onboarding.presentation.screens.OTPScreen
import org.example.project.onboarding.presentation.screens.OnboardingScreen


// Type-safe onboarding navigation graph using Kotlinx Serialization
fun NavGraphBuilder.onboardingNavGraph(navController: NavHostController) {
    composable<OnboardingRoute> {
        var currentPage by remember { mutableStateOf(0) }
        OnboardingScreen(
            currentPage = currentPage,
            onNextClick = {
                if (currentPage < 2) {
                    currentPage++
                } else {
                    navController.navigate(LoginRoute) {
                        popUpTo<OnboardingRoute> { inclusive = true }
                    }
                }
            },
            onSkipClick = {
                navController.navigate(LoginRoute) {
                    popUpTo<OnboardingRoute> { inclusive = true }
                }
            }
        )
    }

    composable<LoginRoute> {
        LoginScreen(
            onLoginClick = { phoneNumber ->
                navController.navigate(OtpRoute(phoneNumber = phoneNumber)) {
                    popUpTo<LoginRoute> { inclusive = true }
                }
            }
        )
    }

    composable<OtpRoute> { backStackEntry ->
        val args = backStackEntry.toRoute<OtpRoute>()

        OTPScreen(
            phoneNumber = args.phoneNumber,
            onVerifyClick = {
                navController.navigate(NameCaptureRoute(phoneNumber = args.phoneNumber)) {
                    popUpTo<OtpRoute> { inclusive = true }
                }
            },
            onResendClick = {
                // Handle resend
            }
        )
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
