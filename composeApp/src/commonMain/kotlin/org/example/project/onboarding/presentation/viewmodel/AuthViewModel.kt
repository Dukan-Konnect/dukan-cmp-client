package org.example.project.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.utils.DataState
import org.example.project.onboarding.domain.repository.AuthRepository

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _effect = Channel<AuthEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

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
        _uiState.update { it.copy(phoneNumber = filteredNumber, error = null) }
    }

    private fun updateOtp(otp: String) {
        val filteredOtp = otp.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otp = filteredOtp, error = null) }
    }

    private fun sendOtp() {
        val phoneNumber = _uiState.value.phoneNumber
        if (phoneNumber.length != 10) {
            _uiState.update { it.copy(error = "Please enter a valid 10-digit phone number") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.requestOtp(phoneNumber)) {
                is DataState.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(AuthEffect.NavigateToOtpScreen(phoneNumber))
                }
                is DataState.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is DataState.Loading -> { }
            }
        }
    }

    private fun verifyOtp() {
        val phoneNumber = _uiState.value.phoneNumber
        val otp = _uiState.value.otp

        if (otp.length != 6) {
            _uiState.update { it.copy(error = "Please enter a valid 6-digit OTP") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.verifyOtp(phoneNumber, otp)) {
                is DataState.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(AuthEffect.NavigateToHome)
                }
                is DataState.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is DataState.Loading -> { }
            }
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
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
    data object NavigateToHome : AuthEffect()
}

data class AuthUiState(
    val phoneNumber: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)