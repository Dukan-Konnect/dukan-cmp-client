package org.example.project.home.presentation.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.home.domain.usecase.CartUseCases

@Immutable
data class ProfileUiState(
    val name: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val cartUseCases: CartUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            cartUseCases.observeCartData()
                .catch { error ->
                    _state.update { it.copy(isLoading = false) }
                }
                .collect { cartData ->
                    _state.update {
                        it.copy(
                            name = cartData.summary?.name,
                            phoneNumber = cartData.summary?.phoneNumber,
                            address = cartData.summary?.address,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateProfile(name: String, phoneNumber: String) {
        viewModelScope.launch {
            try {
                cartUseCases.updateUserInfo(
                    name = name.trim(),
                    phoneNumber = phoneNumber.removePrefix("+91").trim()
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

