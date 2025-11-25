package org.example.project.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.home.domain.repository.CartRepository

data class NameCaptureUiState(
    val name: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class NameCaptureViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NameCaptureUiState())
    val uiState: StateFlow<NameCaptureUiState> = _uiState.asStateFlow()

    fun setPhoneNumber(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = phoneNumber)
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun confirmName(onSuccess: () -> Unit) {
        val current = _uiState.value
        val trimmed = current.name.trim()
        if (trimmed.isEmpty()) {
            _uiState.value = current.copy(error = "Please enter your name")
            return
        }

        _uiState.value = current.copy(isLoading = true, error = null)

        viewModelScope.launch {
            // We only care about first name for display in the cart/summary
            val firstName = trimmed.split(" ").firstOrNull() ?: trimmed

            cartRepository.updateUserName(firstName)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to save name"
                    )
                }
        }
    }
}

