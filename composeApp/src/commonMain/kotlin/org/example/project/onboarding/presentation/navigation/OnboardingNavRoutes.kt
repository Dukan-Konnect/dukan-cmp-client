package org.example.project.onboarding.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
data class OtpRoute(val phoneNumber: String)
