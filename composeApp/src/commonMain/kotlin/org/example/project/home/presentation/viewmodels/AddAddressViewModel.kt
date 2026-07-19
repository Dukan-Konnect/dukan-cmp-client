package org.example.project.home.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.navigation.EditAddressRoute
import org.example.project.core.utils.location.LocationProvider
import org.example.project.home.domain.model.UserLocation
import org.example.project.profile.domain.repository.AddressRepository
import org.example.project.profile.domain.model.SavedAddress
import org.example.project.core.datastore.UserPreferencesRepository

data class AddAddressUiState(
    val isLoading: Boolean = true,
    val formattedAddress: String = "",
    val streetAddress: String = "",
    val houseNumber: String = "",
    val landmark: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

class AddAddressViewModel(
    private val locationProvider: LocationProvider,
    private val addressRepository: AddressRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val addressId = savedStateHandle.toRoute<EditAddressRoute>().addressId
    private val _uiState = MutableStateFlow(AddAddressUiState())
    val uiState: StateFlow<AddAddressUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (addressId.isNotBlank()) {
                val addressResult = addressRepository.getAddressById(addressId)
                addressResult.onSuccess { address ->
                    if (address != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                houseNumber = address.houseNumber,
                                landmark = address.landmark,
                                formattedAddress = address.street,
                                streetAddress = address.street,
                                latitude = address.latitude ?: 0.0,
                                longitude = address.longitude ?: 0.0
                            )
                        }
                        if (address.latitude == null || address.longitude == null || (address.latitude == 0.0 && address.longitude == 0.0)) {
                            fetchLocation()
                        }
                    } else {
                        fetchLocation()
                    }
                }.onFailure {
                    fetchLocation()
                }
            } else {
                fetchLocation()
            }
        }
    }

    fun fetchLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val loc = locationProvider.getCurrentLocation()
            if (loc != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        formattedAddress = loc.address ?: "",
                        streetAddress = loc.streetAddress ?: "",
                        latitude = loc.latitude ?: 0.0,
                        longitude = loc.longitude ?: 0.0
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateHouseNumber(houseNumber: String) {
        _uiState.update { it.copy(houseNumber = houseNumber) }
    }

    fun updateLandmark(landmark: String) {
        _uiState.update { it.copy(landmark = landmark) }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(latitude = latitude, longitude = longitude) }
        viewModelScope.launch {
            val loc = locationProvider.getAddressFromLocation(latitude, longitude)
            if (loc != null) {
                _uiState.update {
                    it.copy(
                        formattedAddress = loc.address ?: "",
                        streetAddress = loc.streetAddress ?: ""
                    )
                }
            }
        }
    }

    fun saveAddress(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val userData = userPreferencesRepository.userData.value
            
            val street = state.formattedAddress.ifBlank { state.streetAddress }
            val fullAddressString = buildString {
                if (state.houseNumber.isNotBlank()) append(state.houseNumber)
                if (street.isNotBlank()) {
                    if (isNotBlank()) append(", ")
                    append(street)
                }
                if (state.landmark.isNotBlank()) {
                    if (isNotBlank()) append(", ")
                    append(state.landmark)
                }
            }
            
            val address = SavedAddress(
                id = addressId, // Generates new ID if blank or handled by backend/DB
                label = "Home",
                houseNumber = state.houseNumber,
                street = street,
                city = "", // City, State can be parsed from formatted address but for now we keep it simple
                state = "",
                landmark = state.landmark,
                phone = userData.phoneNumber,
                latitude = state.latitude,
                longitude = state.longitude,
                isDefault = false
            )
            addressRepository.upsertAddress(address)
            userPreferencesRepository.updateAddress(fullAddressString)
            onSuccess()
        }
    }
}
