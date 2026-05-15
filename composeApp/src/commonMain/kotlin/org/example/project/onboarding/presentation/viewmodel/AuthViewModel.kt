package org.example.project.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.data.repository.AuthRepository
import org.example.project.core.utils.DataState
import kotlinx.coroutines.channels.BufferOverflow
import org.example.project.core.datastore.UserPreferencesRepository

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val prefRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<AuthEffect>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<AuthEffect> = _effect.asSharedFlow()

    var isNewUser = true

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.PhoneNumberChanged -> updatePhoneNumber(intent.phoneNumber)
            is AuthIntent.OtpChanged -> updateOtp(intent.otp)
            is AuthIntent.SendOtpClicked -> sendOtp()
            is AuthIntent.VerifyOtpClicked -> verifyOtp()
            is AuthIntent.ErrorDismissed -> clearError()
        }
    }

    private fun updatePhoneNumber(phoneNumber: String) {
        val filteredNumber = phoneNumber.filter { it.isDigit() }.take(10)
        _uiState.update {
            it.copy(
                phoneNumber = filteredNumber,
                error = null
            )
        }
    }

    private fun updateOtp(otp: String) {
        val filteredOtp = otp.filter { it.isDigit() }.take(6)
        _uiState.update {
            it.copy(
                otp = filteredOtp,
                error = null
            )
        }
    }

    private fun sendOtp() {
        val phoneNumber = _uiState.value.phoneNumber

        if (phoneNumber.length != 10) {
            _uiState.update {
                it.copy(error = "Please enter a valid 10-digit phone number")
            }
            return
        }

        viewModelScope.launch {
            showLoading()

            when (val result = authRepository.requestOtp(phoneNumber)) {
                is DataState.Success -> {
                    hideLoading()
                    _effect.emit(AuthEffect.NavigateToOtpScreen(phoneNumber))
                }

                is DataState.Error -> {
                    showError(result.exception.message ?: "An unexpected error occurred")
                }

                DataState.Loading -> {
                    showLoading()
                }
            }
        }
    }

    private fun verifyOtp() {
        val phoneNumber = _uiState.value.phoneNumber
        val otp = _uiState.value.otp

        if (otp.length != 6) {
            _uiState.update {
                it.copy(error = "Please enter a valid 6-digit OTP")
            }
            return
        }

        viewModelScope.launch {
            showLoading()

            when (val result = authRepository.verifyOtp(phoneNumber, otp)) {
                is DataState.Success -> {
                    isNewUser = result.data.isNewUser
                    hideLoading()
                    if(isNewUser) _effect.emit(AuthEffect.NavigateToNextScreen)
                    else prefRepository.setLoggedIn(true)
                }

                is DataState.Error -> {
                    showError(result.exception.message ?: "An unexpected error occurred")
                }

                DataState.Loading -> {
                    showLoading()
                }
            }
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun showLoading() {
        _uiState.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }
    }

    private fun hideLoading() {
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = message
            )
        }
    }
}

sealed class AuthIntent {
    data class PhoneNumberChanged(val phoneNumber: String) : AuthIntent()
    data class OtpChanged(val otp: String) : AuthIntent()
    data object SendOtpClicked : AuthIntent()
    data object VerifyOtpClicked : AuthIntent()
    data object ErrorDismissed : AuthIntent()
}

sealed class AuthEffect {
    data class NavigateToOtpScreen(val phoneNumber: String) : AuthEffect()
    data object NavigateToNextScreen : AuthEffect()
}

data class AuthUiState(
    val phoneNumber: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
