package org.example.project.home.presentation.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.datastore.UserPreferencesRepository

sealed interface ProfileIntent {
    data object LogoutClicked : ProfileIntent
    data object ManageAddressClicked : ProfileIntent
}

sealed interface ProfileEffect {
    data object NavigateToManageAddress : ProfileEffect
}

@Immutable
data class ProfileUiState(
    val name: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<ProfileEffect> = _effect.asSharedFlow()

    init {
        loadUserProfile()
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.LogoutClicked -> handleLogout()
            ProfileIntent.ManageAddressClicked -> emitEffect(ProfileEffect.NavigateToManageAddress)
        }
    }

    private fun emitEffect(effect: ProfileEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            preferencesRepository.logOut()
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            preferencesRepository.userData.collectLatest { userData ->
                _state.update {
                    it.copy(
                        name = userData.name.takeIf { it.isNotBlank() },
                        phoneNumber = userData.phoneNumber.takeIf { it.isNotBlank() },
                        address = userData.address.takeIf { it.isNotBlank() },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateProfile(name: String, phoneNumber: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.updateName(name.trim())
                preferencesRepository.updatePhoneNumber(phoneNumber.removePrefix("+91").trim())
            } catch (_: Exception) {
                // Handle error
            }
        }
    }
}
