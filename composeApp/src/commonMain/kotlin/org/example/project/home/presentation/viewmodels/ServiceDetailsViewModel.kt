package org.example.project.home.presentation.viewmodels

import androidx.compose.runtime.Immutable
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
import org.example.project.core.log
import org.example.project.home.domain.model.CategoryItem
import org.example.project.home.domain.model.ServiceDetails
import org.example.project.home.domain.model.ServiceSection
import org.example.project.home.domain.model.SubService
import org.example.project.home.domain.repository.ServiceDetailsRepository
import org.example.project.home.domain.model.CartItem
import org.example.project.home.domain.repository.CartRepository

@Immutable
data class ServiceDetailsUiState(
    val isLoading: Boolean = false,
    val serviceDetails: ServiceDetails? = null,
    val selectedCategory: CategoryItem? = null,
    val filteredSections: List<ServiceSection> = emptyList(),
    val errorMessage: String? = null,
    // Per-screen cart state (not global cart totals)
    val screenCartItems: Map<String, Int> = emptyMap(), // subServiceId -> quantity
    val screenCartTotalCents: Long = 0L,
    val screenCartTotalQuantity: Int = 0
)

sealed interface ServiceDetailsEvent {
    data class LoadService(val serviceId: Long) : ServiceDetailsEvent
    data class CategorySelected(val categoryId: String) : ServiceDetailsEvent
    data class SubServiceClicked(val subServiceId: String) : ServiceDetailsEvent
    data class UpdateItemQuantity(val subServiceId: String, val quantity: Int) : ServiceDetailsEvent
    data object ViewCartClicked : ServiceDetailsEvent
    data object BackClicked : ServiceDetailsEvent
    data object Retry : ServiceDetailsEvent
    data object ErrorDismissed : ServiceDetailsEvent
}

sealed interface ServiceDetailsEffect {
    data object NavigateBack : ServiceDetailsEffect
    data object NavigateToSummary : ServiceDetailsEffect
    data class NavigateToSubServiceDetails(val subServiceId: String) : ServiceDetailsEffect
    data class ShowMessage(val message: String) : ServiceDetailsEffect
}

class ServiceDetailsViewModel(
    private val repository: ServiceDetailsRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceDetailsUiState())
    val state: StateFlow<ServiceDetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ServiceDetailsEffect>()
    val effect: SharedFlow<ServiceDetailsEffect> = _effect.asSharedFlow()

    fun onEvent(event: ServiceDetailsEvent) {
        when (event) {
            is ServiceDetailsEvent.LoadService -> loadServiceDetails(event.serviceId)
            is ServiceDetailsEvent.CategorySelected -> { /* handled via UI expand */ }
            is ServiceDetailsEvent.SubServiceClicked -> viewModelScope.launch {
                _effect.emit(ServiceDetailsEffect.NavigateToSubServiceDetails(event.subServiceId))
            }
            is ServiceDetailsEvent.UpdateItemQuantity -> onUpdateItemQuantity(event.subServiceId, event.quantity)
            ServiceDetailsEvent.ViewCartClicked -> viewModelScope.launch { _effect.emit(ServiceDetailsEffect.NavigateToSummary) }
            ServiceDetailsEvent.BackClicked -> viewModelScope.launch { _effect.emit(ServiceDetailsEffect.NavigateBack) }
            ServiceDetailsEvent.Retry -> {
                clearError()
                _state.value.serviceDetails?.id?.let { loadServiceDetails(it) }
            }
            ServiceDetailsEvent.ErrorDismissed -> clearError()
        }
    }

    private fun setLoading(loading: Boolean) { _state.update { it.copy(isLoading = loading) } }
    private fun setError(message: String?) { _state.update { it.copy(errorMessage = message) } }
    private fun clearError() = setError(null)

    private fun loadServiceDetails(serviceId: Long) {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.getServiceDetails(serviceId)
                    .onSuccess { details ->
                        _state.update {
                            it.copy(
                                serviceDetails = details,
                                filteredSections = details.sections,
                                selectedCategory = null
                            )
                        }
                        log("servicedetails", "Loaded: $details")
                    }
                    .onFailure { error ->
                        setError("Failed to load service details: ${error.message}")
                        log("servicedetails", error.message.toString())
                    }
            } catch (e: Exception) {
                setError("Error loading service details: ${e.message}")
                log("servicedetails", e.message.toString())
            } finally { setLoading(false) }
        }
    }

    private fun onUpdateItemQuantity(subServiceId: String, quantity: Int) {
        viewModelScope.launch {
            val current = _state.value
            val details = current.serviceDetails ?: return@launch

            // Update local per-screen map
            val newMap = current.screenCartItems.toMutableMap()
            val previous = newMap[subServiceId] ?: 0
            if (quantity > 0) newMap[subServiceId] = quantity else newMap.remove(subServiceId)

            // Recompute totals for this screen only
            val (totalCents, totalQty) = computeScreenTotals(details, newMap)
            _state.update { it.copy(screenCartItems = newMap, screenCartTotalCents = totalCents, screenCartTotalQuantity = totalQty) }

            // Sync with global cart (Room)
            // Convert subServiceId (String) -> Long if possible
            val productId = subServiceId.toLongOrNull()
            if (productId == null) {
                _effect.emit(ServiceDetailsEffect.ShowMessage("Cannot add item: invalid item id"))
                return@launch
            }
            val sub = findSub(details, subServiceId) ?: run {
                _effect.emit(ServiceDetailsEffect.ShowMessage("Item not found"))
                return@launch
            }

            when {
                quantity <= 0 -> {
                    cartRepository.removeItem(productId)
                }
                previous == 0 -> {
                    // First time add from this screen -> ensure row exists
                    cartRepository.addItem(
                        CartItem(
                            productId = productId,
                            name = sub.title,
                            priceCents = sub.price.toLong() * 100,
                            quantity = quantity,
                            imageUrl = sub.image
                        )
                    )
                }
                else -> {
                    // Update to exact quantity
                    cartRepository.updateItemQuantity(productId, quantity)
                }
            }
        }
    }

    private fun computeScreenTotals(details: ServiceDetails, items: Map<String, Int>): Pair<Long, Int> {
        var cents = 0L
        var qty = 0
        details.sections.forEach { section ->
            section.items.forEach { s ->
                val q = items[s.id] ?: 0
                if (q > 0) {
                    cents += s.price.toLong() * 100L * q
                    qty += q
                }
            }
        }
        return cents to qty
    }

    private fun findSub(details: ServiceDetails, id: String): SubService? {
        details.sections.forEach { section -> section.items.find { it.id == id }?.let { return it } }
        return null
    }
}
