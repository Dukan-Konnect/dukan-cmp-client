package org.example.project.home.presentation.viewmodels

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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.utils.DataState
import org.example.project.core.model.booking.CreateBookingRequest
import org.example.project.booking.domain.repository.BookingRemoteRepository
import org.example.project.home.domain.usecase.CreatePaymentOrderUseCase
import org.example.project.home.presentation.navigation.SummaryRoute

@Immutable
data class SummaryUiState(
    val booking: SummaryRoute? = null,
    val customerName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val timeSlotIso: String? = null,
    val timeSlotFormatted: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val itemTotalCents: Long
        get() = (booking?.providerFee?.toLong() ?: 0L) * 100L

    val taxesCents: Long
        get() = itemTotalCents * 5L / 100L

    val amountToPayCents: Long
        get() = itemTotalCents + taxesCents
}

sealed interface SummaryEvent {
    data class UpdatePhoneNumber(val phoneNumber: String) : SummaryEvent
    data class UpdateAddress(val address: String?) : SummaryEvent
    data class UpdateTimeSlot(val isoString: String?, val formattedString: String?) : SummaryEvent
    data class UpdateName(val name: String?) : SummaryEvent
    data class PaymentSucceeded(val orderId: String) : SummaryEvent
    data object ProceedToPayment : SummaryEvent
    data object BackClicked : SummaryEvent
    data object ErrorDismissed : SummaryEvent
}

sealed interface SummaryEffect {
    data object NavigateBack : SummaryEffect
    data class NavigateToBookings(val message: String) : SummaryEffect
    data class NavigateToPayment(
        val orderId: String,
        val amount: Long,
        val phoneNumber: String
    ) : SummaryEffect

    data class ShowMessage(val message: String) : SummaryEffect
}

class SummaryViewModel(
    savedStateHandle: SavedStateHandle,
    private val createPaymentOrder: CreatePaymentOrderUseCase,
    private val bookingRemoteRepository: BookingRemoteRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val bookingRoute = savedStateHandle.toRoute<SummaryRoute>()

    private val _state = MutableStateFlow(
        SummaryUiState(
            booking = bookingRoute
        )
    )
    val state: StateFlow<SummaryUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SummaryEffect>()
    val effect: SharedFlow<SummaryEffect> = _effect.asSharedFlow()

    init {
        prefillFromUserProfile()
    }

    fun onEvent(event: SummaryEvent) {
        when (event) {
            is SummaryEvent.UpdatePhoneNumber -> {
                _state.update { it.copy(phoneNumber = event.phoneNumber.trim()) }
            }

            is SummaryEvent.UpdateAddress -> {
                _state.update { it.copy(address = event.address?.trim().orEmpty()) }
            }

            is SummaryEvent.UpdateTimeSlot -> {
                _state.update {
                    it.copy(
                        timeSlotIso = event.isoString?.trim(),
                        timeSlotFormatted = event.formattedString?.trim()
                    )
                }
            }

            is SummaryEvent.UpdateName -> {
                _state.update { it.copy(customerName = event.name?.trim().orEmpty()) }
            }

            is SummaryEvent.PaymentSucceeded -> saveBookingAfterPayment(event.orderId)
            SummaryEvent.ProceedToPayment -> proceedToPayment()
            SummaryEvent.BackClicked -> emitEffect(SummaryEffect.NavigateBack)
            SummaryEvent.ErrorDismissed -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun prefillFromUserProfile() {
        viewModelScope.launch {
            userPreferencesRepository.userData
                .map { Triple(it.name.trim(), it.phoneNumber.trim(), it.address.trim()) }
                .distinctUntilChanged()
                .collect { (name, phone, address) ->
                    _state.update { current ->
                        current.copy(
                            customerName = current.customerName.ifBlank { name },
                            phoneNumber = current.phoneNumber.ifBlank { phone },
                            address = current.address.ifBlank { address }
                        )
                    }
                }
        }
    }

    private fun proceedToPayment() {
        val currentState = _state.value
        when {
            currentState.booking == null -> emitEffect(SummaryEffect.ShowMessage("Booking details are missing"))
            currentState.phoneNumber.isBlank() -> emitEffect(SummaryEffect.ShowMessage("Please add phone number"))
            currentState.address.isBlank() -> emitEffect(SummaryEffect.ShowMessage("Please add delivery address"))
            currentState.timeSlotIso.isNullOrBlank() -> emitEffect(SummaryEffect.ShowMessage("Please select time slot"))
            else -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true, errorMessage = null) }
                    createPaymentOrder(currentState.amountToPayCents)
                        .onSuccess { paymentOrder ->
                            _state.update { it.copy(isLoading = false) }
                            _effect.emit(
                                SummaryEffect.NavigateToPayment(
                                    orderId = paymentOrder.orderId,
                                    amount = paymentOrder.amount,
                                    phoneNumber = currentState.phoneNumber
                                )
                            )
                        }
                        .onFailure { error ->
                            val message = "Failed to create payment order: ${error.message}"
                            _state.update { it.copy(isLoading = false, errorMessage = message) }
                            _effect.emit(SummaryEffect.ShowMessage(message))
                        }
                }
            }
        }
    }

    private fun saveBookingAfterPayment(orderId: String) {
        val booking = _state.value.booking ?: return
        val scheduledDateIso = _state.value.timeSlotIso
        val address = _state.value.address.trim()

        if (scheduledDateIso.isNullOrBlank() || address.isBlank()) {
            emitEffect(SummaryEffect.ShowMessage("Missing booking details"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (
                val remoteState = bookingRemoteRepository.createBooking(
                    CreateBookingRequest(
                        subServiceId = booking.subServiceId,
                        providerId = booking.providerId,
                        scheduledDate = scheduledDateIso,
                        address = address
                    )
                )
            ) {
                is DataState.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.emit(SummaryEffect.NavigateToBookings("Booking successful"))
                }

                is DataState.Error -> {
                    val message = remoteState.message.ifBlank {
                        "Failed to confirm booking for order $orderId"
                    }
                    _state.update { it.copy(isLoading = false, errorMessage = message) }
                    _effect.emit(SummaryEffect.ShowMessage(message))
                }

                DataState.Loading -> Unit
            }
        }
    }

    fun formatPrice(cents: Long): String = "₹${cents / 100}"

    private fun emitEffect(effect: SummaryEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
