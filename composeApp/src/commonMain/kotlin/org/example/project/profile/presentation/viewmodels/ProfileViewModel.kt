package org.example.project.profile.presentation.viewmodels

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
import org.example.project.profile.domain.repository.ProfileRepository
import org.example.project.core.utils.DataState
import org.example.project.booking.domain.repository.BookingRepository
import org.example.project.core.utils.SnackbarMessage

private fun String?.orGenericError(): String =
    this?.takeIf { message -> message.isNotBlank() && message != "null" }
        ?: SnackbarMessage.GENERIC_ERROR

sealed interface ProfileIntent {
    data object LogoutClicked : ProfileIntent
    data object ManageAddressClicked : ProfileIntent
}

sealed interface ProfileEffect {
    data object NavigateToManageAddress : ProfileEffect
    data object ProfileUpdated : ProfileEffect
    data class ShowSnackbar(val message: String) : ProfileEffect
}

@Immutable
data class ProfileUiState(
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val profileRepository: ProfileRepository,
    private val bookingRepository: BookingRepository
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
            // Run logout and clearing bookings concurrently to ensure local cache is cleaned promptly
            val logoutJob = launch { preferencesRepository.logOut() }
            val clearBookingsJob = launch { bookingRepository.clearAllBookings() }

            // Wait for both to complete before proceeding
            logoutJob.join()
            clearBookingsJob.join()
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            preferencesRepository.userData.collectLatest { userData ->
                _state.update {
                    it.copy(
                        name = userData.name.takeIf { it.isNotBlank() },
                        email = userData.email.takeIf { it.isNotBlank() },
                        phoneNumber = userData.phoneNumber.takeIf { it.isNotBlank() },
                        address = userData.address.takeIf { it.isNotBlank() },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateProfile(name: String, phoneNumber: String, email: String) {
        val trimmedName = name.trim()
        val normalizedPhone = phoneNumber.removePrefix("+91").trim()
        val trimmedEmail = email.trim()

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                when (val result = profileRepository.updateNameAndEmail(trimmedName, trimmedEmail, normalizedPhone)) {
                    is DataState.Success<*> -> {
                        preferencesRepository.updateName(trimmedName)
                        preferencesRepository.updateEmail(trimmedEmail)
                        preferencesRepository.updatePhoneNumber(normalizedPhone)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                name = trimmedName,
                                email = trimmedEmail,
                                phoneNumber = normalizedPhone
                            )
                        }
                        _effect.emit(ProfileEffect.ProfileUpdated)
                    }
                    is DataState.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        _effect.emit(
                            ProfileEffect.ShowSnackbar(
                                result.message.orGenericError()
                            )
                        )
                    }
                    is DataState.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            } catch (exception: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.emit(
                    ProfileEffect.ShowSnackbar(
                        exception.message.orGenericError()
                    )
                )
            }
        }
    }
}
