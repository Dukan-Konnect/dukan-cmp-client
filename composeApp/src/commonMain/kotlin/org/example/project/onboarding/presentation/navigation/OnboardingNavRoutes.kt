package org.example.project.onboarding.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object AuthRoute
@Serializable
object LoginRoute

@Serializable
data object OtpRoute

@Serializable
object LocationFetchRoute

@Serializable
data class NameCaptureRoute(val phoneNumber: String)
