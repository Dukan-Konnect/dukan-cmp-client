package org.example.project.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.home.domain.location.LocationProvider
import org.example.project.home.domain.repository.CartRepository
import org.example.project.onboarding.presentation.screens.LocationFetchStep
import org.example.project.onboarding.presentation.screens.LocationFetchUiState

class LocationFetchViewModel(
    private val cartRepository: CartRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationFetchUiState())
    val uiState: StateFlow<LocationFetchUiState> = _uiState.asStateFlow()

    fun startLocationFlow() {
        viewModelScope.launch {
            try {
                fetchLocation()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to start location flow: ${e.message}"
                )
            }
        }
    }

    fun onPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            error = "Location permission is required to continue"
        )
    }

    private suspend fun fetchLocation() {
        try {
            // Try to get location, with retries to allow for permission dialog
            var userLocation: org.example.project.home.domain.model.UserLocation? = null
            var attempts = 0
            val maxAttempts = 10 // Retry up to 10 times (10 seconds)

            while (userLocation == null && attempts < maxAttempts) {
                userLocation = locationProvider.getCurrentLocation()
                if (userLocation == null) {
                    delay(1000) // Wait 1 second before retrying
                    attempts++
                }
            }

            if (userLocation == null) {
                _uiState.value = _uiState.value.copy(
                    error = "Unable to get location. Please check location permissions."
                )
                return
            }

            // Simple one-line formatting similar to other apps: "Home - flat, street, area..."
            val formattedAddress = userLocation.address

            cartRepository.updateUserLocation(formattedAddress)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        currentStep = LocationFetchStep.COMPLETED,
                        address = formattedAddress,
                        isCompleted = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to save address: ${exception.message}"
                    )
                }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to fetch location: ${e.message}"
            )
        }
    }
}
