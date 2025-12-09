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
import org.example.project.home.domain.model.ServiceProvider
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
    // Per-screen cart state
    val selectedProviders: Map<String, ServiceProvider> = emptyMap(), // subServiceId -> selected provider
    val availableProviders: Map<String, List<ServiceProvider>> = emptyMap(), // subServiceId -> list of providers
    val expandedSubservices: Set<String> = emptySet(), // subServiceIds with expanded provider list
    val screenCartTotalCents: Long = 0L,
    val screenCartItemCount: Int = 0
)

sealed interface ServiceDetailsEvent {
    data class LoadService(val serviceId: Long) : ServiceDetailsEvent
    data class CategorySelected(val categoryId: String) : ServiceDetailsEvent
    data class SubServiceClicked(val subServiceId: String) : ServiceDetailsEvent
    data class ToggleProviderDropdown(val subServiceId: String) : ServiceDetailsEvent
    data class LoadProviders(val subServiceId: String) : ServiceDetailsEvent
    data class SelectProvider(val subServiceId: String, val provider: ServiceProvider) : ServiceDetailsEvent
    data class RemoveProvider(val subServiceId: String) : ServiceDetailsEvent
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
            is ServiceDetailsEvent.ToggleProviderDropdown -> toggleProviderDropdown(event.subServiceId)
            is ServiceDetailsEvent.LoadProviders -> loadProviders(event.subServiceId)
            is ServiceDetailsEvent.SelectProvider -> selectProvider(event.subServiceId, event.provider)
            is ServiceDetailsEvent.RemoveProvider -> removeProvider(event.subServiceId)
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

    private fun toggleProviderDropdown(subServiceId: String) {
        _state.update { current ->
            val expanded = current.expandedSubservices.toMutableSet()
            if (subServiceId in expanded) {
                expanded.remove(subServiceId)
            } else {
                expanded.add(subServiceId)
                // Load providers if not already loaded
                if (current.availableProviders[subServiceId] == null) {
                    loadProviders(subServiceId)
                }
            }
            current.copy(expandedSubservices = expanded)
        }
    }

    private fun loadProviders(subServiceId: String) {
        viewModelScope.launch {
            try {
                repository.getServiceProviders(subServiceId)
                    .onSuccess { providers ->
                        _state.update { current ->
                            val newMap = current.availableProviders.toMutableMap()
                            newMap[subServiceId] = providers
                            current.copy(availableProviders = newMap)
                        }
                    }
                    .onFailure { error ->
                        _effect.emit(ServiceDetailsEffect.ShowMessage("Failed to load providers: ${error.message}"))
                        log("servicedetails", "Failed to load providers: ${error.message}")
                    }
            } catch (e: Exception) {
                _effect.emit(ServiceDetailsEffect.ShowMessage("Error loading providers: ${e.message}"))
                log("servicedetails", "Error loading providers: ${e.message}")
            }
        }
    }

    private fun selectProvider(subServiceId: String, provider: ServiceProvider) {
        viewModelScope.launch {
            val details = _state.value.serviceDetails ?: return@launch
            val subService = findSub(details, subServiceId) ?: run {
                _effect.emit(ServiceDetailsEffect.ShowMessage("Service not found"))
                return@launch
            }

            val currentProvider = _state.value.selectedProviders[subServiceId]

            // If clicking on the already selected provider, unselect it
            if (currentProvider?.id == provider.id) {
                removeProvider(subServiceId)
                return@launch
            }

            // Update UI state
            _state.update { current ->
                val newProviders = current.selectedProviders.toMutableMap()
                newProviders[subServiceId] = provider
                val (totalCents, itemCount) = computeScreenTotals(details, newProviders)
                current.copy(
                    selectedProviders = newProviders,
                    screenCartTotalCents = totalCents,
                    screenCartItemCount = itemCount
                )
            }

            // Update Room database
            cartRepository.upsertItem(
                CartItem(
                    productId = subServiceId,
                    name = subService.title,
                    priceCents = subService.price.toLong() * 100,
                    imageUrl = subService.image,
                    providerId = provider.id,
                    providerName = provider.name,
                    providerImageUrl = provider.imageUrl,
                    providerPhoneNumber = provider.phoneNumber,
                    providerRating = provider.rating,
                    providerFeeCents = provider.fee.toLong() * 100 // Convert to cents
                )
            )
        }
    }

    private fun removeProvider(subServiceId: String) {
        viewModelScope.launch {
            val details = _state.value.serviceDetails ?: return@launch

            // Update UI state
            _state.update { current ->
                val newProviders = current.selectedProviders.toMutableMap()
                newProviders.remove(subServiceId)
                val (totalCents, itemCount) = computeScreenTotals(details, newProviders)
                current.copy(
                    selectedProviders = newProviders,
                    screenCartTotalCents = totalCents,
                    screenCartItemCount = itemCount
                )
            }

            // Remove from Room database
            cartRepository.removeItem(subServiceId)
        }
    }

    private fun computeScreenTotals(details: ServiceDetails, selectedProviders: Map<String, ServiceProvider>): Pair<Long, Int> {
        var cents = 0L
        var count = 0
        details.sections.forEach { section ->
            section.items.forEach { sub ->
                val provider = selectedProviders[sub.id]
                if (provider != null) {
                    // Convert fee to cents for calculation (fee is in rupees)
                    cents += provider.fee.toLong() * 100
                    count++
                }
            }
        }
        return cents to count
    }

    private fun findSub(details: ServiceDetails, id: String): SubService? {
        details.sections.forEach { section -> section.items.find { it.id == id }?.let { return it } }
        return null
    }
}
