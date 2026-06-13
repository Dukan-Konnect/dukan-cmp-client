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
data class BookingsRoute(val successMessage: String? = null)

@Serializable
object ProfileRoute

@Serializable
object ManageAddressRoute

@Serializable
data class EditAddressRoute(val addressId: String = "")

@Serializable
data class BookingDetailRoute(val bookingId: String)

@Serializable
data class CancelBookingRoute(val bookingId: String)

@Serializable
data class RescheduleBookingRoute(val bookingId: String)
