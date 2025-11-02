package org.example.project.onboarding.presentation.viewmodel

import SendOtpUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.core.log
import org.example.project.core.settings.AuthSettings
import org.example.project.onboarding.domain.usecase.VerifyOtpUseCase

class AuthViewModel(
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val authSettings: AuthSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onPhoneNumberChange(phoneNumber: String) {
        val filteredNumber = phoneNumber.filter { it.isDigit() }.take(10)
        _uiState.value = _uiState.value.copy(
            phoneNumber = filteredNumber,
            error = null
        )
    }

    fun sendOtp(onSuccess: (String) -> Unit) {
        val phoneNumber = _uiState.value.phoneNumber
        if (phoneNumber.length != 10) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter a valid 10-digit phone number"
            )
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            sendOtpUseCase("+91$phoneNumber")
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, otpSent = true)
                    onSuccess(phoneNumber)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to send OTP"
                    )
                    log("authviewmodel", "verifyOtp: ${exception.message} $phoneNumber")
                }
        }
    }

    fun onOtpChange(otp: String) {
        val filteredOtp = otp.filter { it.isDigit() }.take(6)
        _uiState.value = _uiState.value.copy(
            otp = filteredOtp,
            error = null
        )
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        val phoneNumber = _uiState.value.phoneNumber
        val otp = _uiState.value.otp
        if (otp.length != 6) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter a valid 6-digit OTP"
            )
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            verifyOtpUseCase("+91$phoneNumber", otp)
                .onSuccess {
                    // persist login state
                    authSettings.setLoggedIn(true)
                    _uiState.value = _uiState.value.copy(isLoading = false, otpVerified = true)
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to verify OTP"
                    )
                    log("authviewmodel", "verifyOtp: ${exception.message} $phoneNumber $otp")
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

// Combined UI state for login and OTP

data class AuthUiState(
    val phoneNumber: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val otpSent: Boolean = false,
    val otpVerified: Boolean = false
)
