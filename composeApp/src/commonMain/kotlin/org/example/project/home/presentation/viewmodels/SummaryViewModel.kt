package org.example.project.home.presentation.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.home.domain.model.CartItem
import org.example.project.home.domain.model.CartSummary
import org.example.project.home.domain.model.CartTotals
import org.example.project.home.domain.usecase.CartUseCases

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
    data class UpdateItemQuantity(val productId: Long, val quantity: Int) : SummaryEvent
    data class RemoveItem(val productId: Long) : SummaryEvent
    data class UpdatePhoneNumber(val phoneNumber: String) : SummaryEvent
    data class UpdateAddress(val address: String?) : SummaryEvent
    data class UpdateTimeSlot(val timeSlot: String?) : SummaryEvent
    data object ClearCart : SummaryEvent
    data object ProceedToPayment : SummaryEvent
    data object BackClicked : SummaryEvent
    data object ErrorDismissed : SummaryEvent
}

sealed interface SummaryEffect {
    data object NavigateBack : SummaryEffect
    data object NavigateToPayment : SummaryEffect
    data class ShowMessage(val message: String) : SummaryEffect
}

class SummaryViewModel(
    private val cartUseCases: CartUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(SummaryUiState())
    val state: StateFlow<SummaryUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SummaryEffect>()
    val effect: SharedFlow<SummaryEffect> = _effect.asSharedFlow()

    init {
        observeCartData()
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

    fun onEvent(event: SummaryEvent) {
        when (event) {
            is SummaryEvent.UpdateItemQuantity -> updateItemQuantity(event.productId, event.quantity)
            is SummaryEvent.RemoveItem -> removeItem(event.productId)
            is SummaryEvent.UpdatePhoneNumber -> updatePhoneNumber(event.phoneNumber)
            is SummaryEvent.UpdateAddress -> updateAddress(event.address)
            is SummaryEvent.UpdateTimeSlot -> updateTimeSlot(event.timeSlot)
            SummaryEvent.ClearCart -> clearCart()
            SummaryEvent.ProceedToPayment -> proceedToPayment()
            SummaryEvent.BackClicked -> navigateBack()
            SummaryEvent.ErrorDismissed -> dismissError()
        }
    }

    private fun updateItemQuantity(productId: Long, quantity: Int) {
        viewModelScope.launch {
            setLoading(true)
            cartUseCases.updateItemQuantity(productId, quantity)
                .onSuccess {
                    // Cart data will update automatically via Flow
                }
                .onFailure { error ->
                    setError("Failed to update quantity: ${error.message}")
                }
            setLoading(false)
        }
    }

    private fun removeItem(productId: Long) {
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
                    _effect.emit(SummaryEffect.NavigateToPayment)
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

    // Convenience methods for UI
    fun incrementQuantity(productId: Long) {
        viewModelScope.launch {
            cartUseCases.incrementItemQuantity(productId)
        }
    }

    fun decrementQuantity(productId: Long) {
        viewModelScope.launch {
            cartUseCases.decrementItemQuantity(productId)
        }
    }

    fun formatPrice(cents: Long): String {
        return "₹${cents / 100}"
    }

    fun formatPriceWithDecimals(cents: Long): String {
        val rupees = cents / 100
        val paise = cents % 100
        return if (paise > 0) "₹$rupees.$paise" else "₹$rupees"
    }
}
