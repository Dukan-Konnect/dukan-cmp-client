package org.example.project.profile.presentation.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.navigation.EditAddressRoute
import org.example.project.profile.domain.model.SavedAddress
import org.example.project.profile.domain.repository.AddressRepository

sealed interface EditAddressIntent {
    data class HouseNumberChanged(val value: String) : EditAddressIntent
    data class StreetChanged(val value: String) : EditAddressIntent
    data class CityChanged(val value: String) : EditAddressIntent
    data class StateChanged(val value: String) : EditAddressIntent
    data class LandmarkChanged(val value: String) : EditAddressIntent
    data class SaveAsChanged(val value: String) : EditAddressIntent
    data class DefaultChanged(val value: Boolean) : EditAddressIntent
    data object SaveClicked : EditAddressIntent
    data object BackClicked : EditAddressIntent
}

sealed interface EditAddressEffect {
    data object NavigateBack : EditAddressEffect
}

@Immutable
data class EditAddressUiState(
    val addressId: String = "",
    val houseNumber: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val landmark: String = "",
    val saveAs: String = "Home",
    val isDefault: Boolean = false,
    val phone: String = "",
    val isLoading: Boolean = false
)

class EditAddressViewModel(
    savedStateHandle: SavedStateHandle,
    private val addressRepository: AddressRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val addressId: String = savedStateHandle.toRoute<EditAddressRoute>().addressId

    private val _uiState = MutableStateFlow(EditAddressUiState(isLoading = true))
    val uiState: StateFlow<EditAddressUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<EditAddressEffect>()
    val effect: SharedFlow<EditAddressEffect> = _effect.asSharedFlow()

    init {
        loadInitialState()
    }

    fun handleIntent(intent: EditAddressIntent) {
        when (intent) {
            EditAddressIntent.BackClicked -> emitEffect(EditAddressEffect.NavigateBack)
            is EditAddressIntent.HouseNumberChanged -> _uiState.update { it.copy(houseNumber = intent.value) }
            is EditAddressIntent.StreetChanged -> _uiState.update { it.copy(street = intent.value) }
            is EditAddressIntent.CityChanged -> _uiState.update { it.copy(city = intent.value) }
            is EditAddressIntent.StateChanged -> _uiState.update { it.copy(state = intent.value) }
            is EditAddressIntent.LandmarkChanged -> _uiState.update { it.copy(landmark = intent.value) }
            is EditAddressIntent.SaveAsChanged -> _uiState.update { it.copy(saveAs = intent.value) }
            is EditAddressIntent.DefaultChanged -> _uiState.update { it.copy(isDefault = intent.value) }
            EditAddressIntent.SaveClicked -> saveAddress()
        }
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            val userData = userPreferencesRepository.userData.value
            val savedAddress = addressId.takeIf { it.isNotBlank() }?.let {
                addressRepository.getAddressById(it).getOrNull()
            }

            _uiState.update {
                it.copy(
                    addressId = savedAddress?.id.orEmpty(),
                    houseNumber = savedAddress?.houseNumber.orEmpty(),
                    street = savedAddress?.street.orEmpty(),
                    city = savedAddress?.city.orEmpty(),
                    state = savedAddress?.state.orEmpty(),
                    landmark = savedAddress?.landmark.orEmpty(),
                    saveAs = savedAddress?.label ?: "Home",
                    isDefault = savedAddress?.isDefault ?: false,
                    phone = savedAddress?.phone ?: userData.phoneNumber,
                    isLoading = false
                )
            }
        }
    }

    private fun saveAddress() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.houseNumber.isBlank() || state.street.isBlank() || state.city.isBlank() || state.state.isBlank()) {
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            val result = addressRepository.upsertAddress(
                SavedAddress(
                    id = state.addressId,
                    label = state.saveAs,
                    houseNumber = state.houseNumber.trim(),
                    street = state.street.trim(),
                    city = state.city.trim(),
                    state = state.state.trim(),
                    landmark = state.landmark.trim(),
                    phone = state.phone.trim(),
                    isDefault = state.isDefault
                )
            )

            _uiState.update { it.copy(isLoading = false) }
            if (result.isSuccess) {
                emitEffect(EditAddressEffect.NavigateBack)
            }
        }
    }

    private fun emitEffect(effect: EditAddressEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
