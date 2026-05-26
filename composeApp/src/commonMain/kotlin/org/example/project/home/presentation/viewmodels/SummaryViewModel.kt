package org.example.project.home.presentation.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.utils.AddressFormatter
import org.example.project.core.utils.DataState
import org.example.project.home.domain.model.CartItem
import org.example.project.home.domain.model.CartSummary
import org.example.project.home.domain.model.CartTotals
import org.example.project.home.domain.model.CreateBookingRequest
import org.example.project.home.domain.usecase.CartUseCases
import org.example.project.home.domain.repository.BookingRemoteRepository

@Immutable
data class SummaryUiState(
    val cartItems: List<CartItem> = emptyList(),
    val cartSummary: CartSummary? = null,
    val cartTotals: CartTotals? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCartEmpty: Boolean = true
)

sealed interface SummaryEvent {
    data class RemoveItem(val productId: String) : SummaryEvent
    data class UpdatePhoneNumber(val phoneNumber: String) : SummaryEvent
    data class UpdateAddress(val address: String?) : SummaryEvent
    data class UpdateTimeSlot(val timeSlot: String?) : SummaryEvent
    data class UpdateName(val name: String?) : SummaryEvent
    data object ClearCart : SummaryEvent
    data object ProceedToPayment : SummaryEvent
    data object BackClicked : SummaryEvent
    data object ErrorDismissed : SummaryEvent
}

sealed interface SummaryEffect {
    data object NavigateBack : SummaryEffect
    data class NavigateToPayment(val orderId: String, val amount: Long) : SummaryEffect
    data class ShowMessage(val message: String) : SummaryEffect
}

class SummaryViewModel(
    private val cartUseCases: CartUseCases,
    private val createPaymentOrder: org.example.project.home.domain.usecase.CreatePaymentOrderUseCase,
    private val bookingRepository: org.example.project.home.domain.repository.BookingRepository,
    private val bookingRemoteRepository: BookingRemoteRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SummaryUiState())
    val state: StateFlow<SummaryUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SummaryEffect>()
    val effect: SharedFlow<SummaryEffect> = _effect.asSharedFlow()

    init {
        observeCartData()
        prefillAddressFromUserLocation()
    }

    private fun observeCartData() {
        viewModelScope.launch {
            cartUseCases.observeCartData()
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load cart: ${error.message}"
                        )
                    }
                }
                .collect { cartData ->
                    _state.update {
                        it.copy(
                            cartItems = cartData.items,
                            cartSummary = cartData.summary,
                            cartTotals = cartData.totals,
                            isCartEmpty = cartData.items.isEmpty(),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    private fun prefillAddressFromUserLocation() {
        viewModelScope.launch {
            userPreferencesRepository.userData
                .map { Triple(it.name.trim(), it.phoneNumber.trim(), it.address.trim()) }
                .distinctUntilChanged()
                .collect { user ->
                    val prefName = user.first
                    val prefPhone = user.second
                    val prefAddress = user.third

                    val current = _state.value.cartSummary

                    if (prefPhone.isNotBlank() && current?.phoneNumber.isNullOrBlank()) {
                        cartUseCases.updatePhoneNumber(prefPhone)
                    }

                    if (prefName.isNotBlank() && current?.name.isNullOrBlank()) {
                        cartUseCases.updateUserName(prefName)
                    }

                    if (prefAddress.isNotBlank() && current?.address.isNullOrBlank()) {
                        cartUseCases.updateDeliveryAddress(prefAddress)
                    }
                }
        }
    }

    fun onEvent(event: SummaryEvent) {
        when (event) {
            is SummaryEvent.RemoveItem -> removeItem(event.productId)
            is SummaryEvent.UpdatePhoneNumber -> updatePhoneNumber(event.phoneNumber)
            is SummaryEvent.UpdateAddress -> updateAddress(event.address)
            is SummaryEvent.UpdateTimeSlot -> updateTimeSlot(event.timeSlot)
            is SummaryEvent.UpdateName -> updateName(event.name)
            SummaryEvent.ClearCart -> clearCart()
            SummaryEvent.ProceedToPayment -> proceedToPayment()
            SummaryEvent.BackClicked -> navigateBack()
            SummaryEvent.ErrorDismissed -> dismissError()
        }
    }


    private fun removeItem(productId: String) {
        viewModelScope.launch {
            setLoading(true)
            cartUseCases.removeItemFromCart(productId)
                .onSuccess {
                    _effect.emit(SummaryEffect.ShowMessage("Item removed from cart"))
                }
                .onFailure { error ->
                    setError("Failed to remove item: ${error.message}")
                }
            setLoading(false)
        }
    }

    private fun updatePhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            cartUseCases.updatePhoneNumber(phoneNumber)
                .onSuccess {
                    _effect.emit(SummaryEffect.ShowMessage("Phone number updated"))
                }
                .onFailure { error ->
                    setError("Failed to update phone: ${error.message}")
                }
        }
    }

    private fun updateAddress(address: String?) {
        viewModelScope.launch {
            cartUseCases.updateDeliveryAddress(address)
                .onSuccess {
                    _effect.emit(SummaryEffect.ShowMessage("Address updated"))
                }
                .onFailure { error ->
                    setError("Failed to update address: ${error.message}")
                }
        }
    }

    private fun updateTimeSlot(timeSlot: String?) {
        viewModelScope.launch {
            cartUseCases.updateTimeSlot(timeSlot)
                .onSuccess {
                    _effect.emit(SummaryEffect.ShowMessage("Time slot updated"))
                }
                .onFailure { error ->
                    setError("Failed to update time slot: ${error.message}")
                }
        }
    }

    private fun updateName(name: String?) {
        viewModelScope.launch {
            cartUseCases.updateUserName(name)
                .onSuccess {
                    _effect.emit(SummaryEffect.ShowMessage("Name updated"))
                }
                .onFailure { error ->
                    setError("Failed to update name: ${error.message}")
                }
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            setLoading(true)
            cartUseCases.clearCartItems()
                .onSuccess {
                    _effect.emit(SummaryEffect.ShowMessage("Cart cleared"))
                }
                .onFailure { error ->
                    setError("Failed to clear cart: ${error.message}")
                }
            setLoading(false)
        }
    }

    private fun proceedToPayment() {
        viewModelScope.launch {
            val summary = _state.value.cartSummary
            val items = _state.value.cartItems
            val totals = _state.value.cartTotals

            when {
                items.isEmpty() -> {
                    _effect.emit(SummaryEffect.ShowMessage("Cart is empty"))
                }
                summary?.phoneNumber.isNullOrBlank() -> {
                    _effect.emit(SummaryEffect.ShowMessage("Please add phone number"))
                }
                summary.address.isNullOrBlank() -> {
                    _effect.emit(SummaryEffect.ShowMessage("Please add delivery address"))
                }
                summary.timeSlot.isNullOrBlank() -> {
                    _effect.emit(SummaryEffect.ShowMessage("Please select time slot"))
                }
                else -> {
                    // Create Razorpay order
                    setLoading(true)
                    val amountToPay = totals?.amountToPayCents ?: 0

                    createPaymentOrder(amountToPay)
                        .onSuccess { paymentOrder ->
                            setLoading(false)
                            _effect.emit(SummaryEffect.NavigateToPayment(paymentOrder.orderId, paymentOrder.amount))
                        }
                        .onFailure { error ->
                            setLoading(false)
                            _effect.emit(SummaryEffect.ShowMessage("Failed to create payment order: ${error.message}"))
                        }
                }
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.emit(SummaryEffect.NavigateBack)
        }
    }

    private fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun setLoading(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }

    private fun setError(message: String) {
        _state.update { it.copy(errorMessage = message, isLoading = false) }
    }


    fun formatPrice(cents: Long): String {
        return "₹${cents / 100}"
    }

    fun formatPriceWithDecimals(cents: Long): String {
        val rupees = cents / 100
        val paise = cents % 100
        return if (paise > 0) "₹$rupees.$paise" else "₹$rupees"
    }

    @OptIn(kotlin.time.ExperimentalTime::class)
    fun saveBookingsAfterPayment(orderId: String) {
        viewModelScope.launch {
            if (_state.value.isLoading) return@launch

            val items = _state.value.cartItems
            val summary = _state.value.cartSummary

            if (items.isEmpty()) return@launch

            val scheduledDate = summary?.timeSlot
            val address = summary?.address?.let { AddressFormatter.formatFullAddress(it) }

            if (scheduledDate.isNullOrBlank() || address.isNullOrBlank()) {
                _effect.emit(SummaryEffect.ShowMessage("Missing booking details (address/time slot)."))
                return@launch
            }

            setLoading(true)

            var failedCount = 0
            var firstError: String? = null

            items.forEach { cartItem ->
                when (val remoteState = bookingRemoteRepository.createBooking(
                    CreateBookingRequest(
                        subServiceId = cartItem.productId,
                        providerId = cartItem.providerId,
                        scheduledDate = scheduledDate,
                        address = address
                    )
                )) {
                    is DataState.Success -> {
                        val booking = remoteState.data
                        val localResult = bookingRepository.createBooking(booking)
                        if (localResult.isSuccess) {
                            cartUseCases.removeItemFromCart(cartItem.productId)
                        } else {
                            failedCount++
                            if (firstError == null) {
                                firstError = localResult.exceptionOrNull()?.message ?: "Failed to save booking locally"
                            }
                        }
                    }

                    is DataState.Error -> {
                        failedCount++
                        if (firstError == null) {
                            firstError = remoteState.message
                        }
                    }

                    DataState.Loading -> Unit
                }
            }

            setLoading(false)

            if (failedCount > 0) {
                _effect.emit(
                    SummaryEffect.ShowMessage(
                        "Booking sync failed for $failedCount item(s). ${firstError ?: ""}".trim()
                    )
                )
            } else {
                _effect.emit(SummaryEffect.ShowMessage("Booking successful"))
            }
        }
    }
}
