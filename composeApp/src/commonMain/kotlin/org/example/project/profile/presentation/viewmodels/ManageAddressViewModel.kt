package org.example.project.profile.presentation.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.profile.domain.model.SavedAddress
import org.example.project.profile.domain.repository.AddressRepository

sealed interface ManageAddressIntent {
    data object AddAddressClicked : ManageAddressIntent
    data class EditAddressClicked(val addressId: String) : ManageAddressIntent
    data class DeleteAddressClicked(val id: String) : ManageAddressIntent
    data object BackClicked : ManageAddressIntent
}

sealed interface ManageAddressEffect {
    data object NavigateBack : ManageAddressEffect
    data object NavigateToAddAddress : ManageAddressEffect
    data class NavigateToEditAddress(val addressId: String) : ManageAddressEffect
}

@Immutable
data class ManageAddressUiState(
    val addresses: List<SavedAddress> = emptyList(),
    val isLoading: Boolean = false
)

class ManageAddressViewModel(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageAddressUiState())
    val uiState: StateFlow<ManageAddressUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ManageAddressEffect>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<ManageAddressEffect> = _effect.asSharedFlow()

    init {
        observeAddresses()
    }

    fun handleIntent(intent: ManageAddressIntent) {
        when (intent) {
            ManageAddressIntent.AddAddressClicked -> emitEffect(ManageAddressEffect.NavigateToAddAddress)
            is ManageAddressIntent.EditAddressClicked -> emitEffect(ManageAddressEffect.NavigateToEditAddress(intent.addressId))
            is ManageAddressIntent.DeleteAddressClicked -> deleteAddress(intent.id)
            ManageAddressIntent.BackClicked -> emitEffect(ManageAddressEffect.NavigateBack)
        }
    }

    private fun observeAddresses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            addressRepository.observeAddresses()
                .catch {
                    _uiState.update { state -> state.copy(addresses = emptyList(), isLoading = false) }
                }
                .collect { addresses ->
                    _uiState.update { it.copy(addresses = addresses, isLoading = false) }
                }
        }
    }

    private fun deleteAddress(id: String) {
        viewModelScope.launch {
            addressRepository.deleteAddress(id)
        }
    }

    private fun emitEffect(effect: ManageAddressEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
