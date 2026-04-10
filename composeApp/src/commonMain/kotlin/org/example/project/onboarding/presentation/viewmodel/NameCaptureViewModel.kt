package org.example.project.onboarding.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.data.repository.ProfileRepository
import org.example.project.core.utils.DataState
import org.example.project.onboarding.presentation.navigation.NameCaptureRoute

class NameCaptureViewModel(
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(NameCaptureUiState())
    val uiState: StateFlow<NameCaptureUiState> = _uiState.asStateFlow()

    private val phoneNumber: String = savedStateHandle.toRoute<NameCaptureRoute>().phoneNumber
    private val _effect = MutableSharedFlow<NameCaptureEffect>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<NameCaptureEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: NameCaptureIntent) {
        when (intent) {
            is NameCaptureIntent.NameChanged -> updateName(intent.name)
            is NameCaptureIntent.EmailChanged -> updateEmail(intent.email)
            is NameCaptureIntent.SubmitClicked -> submitProfile()
            is NameCaptureIntent.ErrorDismissed -> clearError()
        }
    }

    private fun updateName(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    private fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    private fun submitProfile() {
        val currentName = _uiState.value.name.trim()
        val currentEmail = _uiState.value.email.trim()

        if (currentName.isEmpty()) {
            showError("Please enter your name")
            return
        }

        if (currentEmail.isEmpty() || !emailRegex.matches(currentEmail)) {
            showError("Please enter a valid email address")
            return
        }

        viewModelScope.launch {
            showLoading()

            when (val result = profileRepository.updateNameAndEmail(currentName, currentEmail, phoneNumber)) {
                is DataState.Success<*> -> {
                    hideLoading()
                    _effect.emit(NameCaptureEffect.NavigateToNextScreen)
                }
                is DataState.Error -> {
                    showError(result.exception.message ?: "Failed to save profile")
                }
                is DataState.Loading -> {
                    showLoading()
                }
            }
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun showLoading() {
        _uiState.update { it.copy(isLoading = true, error = null) }
    }

    private fun hideLoading() {
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(isLoading = false, error = message) }
    }

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-z]{2,}\$".toRegex()
}

// --- MVI Components ---

sealed class NameCaptureIntent {
    data class NameChanged(val name: String) : NameCaptureIntent()
    data class EmailChanged(val email: String) : NameCaptureIntent()
    data object SubmitClicked : NameCaptureIntent()
    data object ErrorDismissed : NameCaptureIntent()
}

sealed class NameCaptureEffect {
    data object NavigateToNextScreen : NameCaptureEffect()
}

data class NameCaptureUiState(
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)