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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.log
import org.example.project.home.domain.model.ServiceDetails
import org.example.project.home.domain.model.ServiceProvider
import org.example.project.home.domain.model.SubService
import org.example.project.home.domain.repository.ServiceDetailsRepository
import org.example.project.home.presentation.navigation.ServiceDetailRoute
import org.example.project.home.presentation.navigation.SummaryRoute

@Immutable
data class ServiceDetailsUiState(
    val isLoading: Boolean = false,
    val serviceDetails: ServiceDetails? = null,
    val errorMessage: String? = null,
    val availableProviders: Map<String, List<ServiceProvider>> = emptyMap(),
    val expandedSubservices: Set<String> = emptySet(),
    val selectedSubServiceId: String? = null,
    val selectedProvider: ServiceProvider? = null
) {
    val filteredSections = serviceDetails?.sections.orEmpty()
    val selectedProviderFeeCents: Long = (selectedProvider?.fee?.toLong() ?: 0L) * 100L
    val hasSelection: Boolean = selectedSubServiceId != null && selectedProvider != null
}

sealed interface ServiceDetailsEvent {
    data class SubServiceClicked(val subServiceId: String) : ServiceDetailsEvent
    data class ToggleProviderDropdown(val subServiceId: String) : ServiceDetailsEvent
    data class SelectProvider(val subServiceId: String, val provider: ServiceProvider) : ServiceDetailsEvent
    data object BookNowClicked : ServiceDetailsEvent
    data object BackClicked : ServiceDetailsEvent
    data object Retry : ServiceDetailsEvent
    data object ErrorDismissed : ServiceDetailsEvent
}

sealed interface ServiceDetailsEffect {
    data object NavigateBack : ServiceDetailsEffect
    data class NavigateToSummary(val route: SummaryRoute) : ServiceDetailsEffect
    data class NavigateToSubServiceDetails(val subServiceId: String) : ServiceDetailsEffect
    data class ShowMessage(val message: String) : ServiceDetailsEffect
}

class ServiceDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ServiceDetailsRepository
) : ViewModel() {

    private val serviceId = savedStateHandle.toRoute<ServiceDetailRoute>().serviceId

    private val _state = MutableStateFlow(ServiceDetailsUiState())
    val state: StateFlow<ServiceDetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ServiceDetailsEffect>()
    val effect: SharedFlow<ServiceDetailsEffect> = _effect.asSharedFlow()

    init {
        loadServiceDetails()
    }

    fun onEvent(event: ServiceDetailsEvent) {
        when (event) {
            is ServiceDetailsEvent.SubServiceClicked -> emitEffect(
                ServiceDetailsEffect.NavigateToSubServiceDetails(event.subServiceId)
            )

            is ServiceDetailsEvent.ToggleProviderDropdown -> toggleProviderDropdown(event.subServiceId)
            is ServiceDetailsEvent.SelectProvider -> selectProvider(event.subServiceId, event.provider)
            ServiceDetailsEvent.BookNowClicked -> navigateToSummary()
            ServiceDetailsEvent.BackClicked -> emitEffect(ServiceDetailsEffect.NavigateBack)
            ServiceDetailsEvent.Retry -> {
                clearError()
                loadServiceDetails()
            }

            ServiceDetailsEvent.ErrorDismissed -> clearError()
        }
    }

    private fun loadServiceDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.getServiceDetails(serviceId)
                    .onSuccess { details ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                serviceDetails = details
                            )
                        }
                    }
                    .onFailure { error ->
                        val message = "Failed to load service details: ${error.message}"
                        _state.update { it.copy(isLoading = false, errorMessage = message) }
                        log("servicedetails", message)
                    }
            } catch (e: Exception) {
                val message = "Error loading service details: ${e.message}"
                _state.update { it.copy(isLoading = false, errorMessage = message) }
                log("servicedetails", message)
            }
        }
    }

    private fun toggleProviderDropdown(subServiceId: String) {
        val shouldLoadProviders = _state.value.availableProviders[subServiceId] == null
        _state.update { current ->
            val expanded = current.expandedSubservices.toMutableSet()
            if (!expanded.add(subServiceId)) {
                expanded.remove(subServiceId)
            }
            current.copy(expandedSubservices = expanded)
        }
        if (shouldLoadProviders) {
            loadProviders(subServiceId)
        }
    }

    private fun loadProviders(subServiceId: String) {
        viewModelScope.launch {
            try {
                repository.getServiceProviders(subServiceId)
                    .onSuccess { providers ->
                        _state.update { current ->
                            current.copy(
                                availableProviders = current.availableProviders + (subServiceId to providers)
                            )
                        }
                    }
                    .onFailure { error ->
                        emitEffect(
                            ServiceDetailsEffect.ShowMessage(
                                "Failed to load providers: ${error.message}"
                            )
                        )
                    }
            } catch (e: Exception) {
                emitEffect(ServiceDetailsEffect.ShowMessage("Error loading providers: ${e.message}"))
            }
        }
    }

    private fun selectProvider(subServiceId: String, provider: ServiceProvider) {
        val currentState = _state.value
        val sameSelection = currentState.selectedSubServiceId == subServiceId &&
            currentState.selectedProvider?.id == provider.id

        _state.update {
            if (sameSelection) {
                it.copy(
                    selectedSubServiceId = null,
                    selectedProvider = null
                )
            } else {
                it.copy(
                    selectedSubServiceId = subServiceId,
                    selectedProvider = provider
                )
            }
        }
    }

    private fun navigateToSummary() {
        val details = _state.value.serviceDetails ?: return
        val subServiceId = _state.value.selectedSubServiceId
        val provider = _state.value.selectedProvider

        if (subServiceId == null || provider == null) {
            emitEffect(ServiceDetailsEffect.ShowMessage("Select a provider to continue"))
            return
        }

        val subService = findSub(details, subServiceId)
        if (subService == null) {
            emitEffect(ServiceDetailsEffect.ShowMessage("Selected service is unavailable"))
            return
        }

        emitEffect(
            ServiceDetailsEffect.NavigateToSummary(
                SummaryRoute(
                    serviceId = details.id,
                    serviceTitle = details.title,
                    subServiceId = subService.id,
                    subServiceTitle = subService.title,
                    subServiceImage = subService.image,
                    subServicePrice = subService.price,
                    providerId = provider.id,
                    providerName = provider.name,
                    providerImageUrl = provider.imageUrl,
                    providerPhoneNumber = provider.phoneNumber,
                    providerRating = provider.rating,
                    providerFee = provider.fee
                )
            )
        )
    }

    private fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun findSub(details: ServiceDetails, id: String): SubService? {
        details.sections.forEach { section ->
            section.items.firstOrNull { it.id == id }?.let { return it }
        }
        return null
    }

    private fun emitEffect(effect: ServiceDetailsEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
