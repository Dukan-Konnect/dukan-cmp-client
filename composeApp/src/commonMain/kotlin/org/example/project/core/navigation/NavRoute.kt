package org.example.project.core.navigation

import kotlinx.serialization.Serializable

// Type-safe navigation routes using Kotlinx Serialization
// This provides compile-time safety and automatic argument passing

// Onboarding feature routes
@Serializable
object OnboardingRoute

@Serializable
object HomeRoute

@Serializable
object BookingsRoute

@Serializable
object ProfileRoute


