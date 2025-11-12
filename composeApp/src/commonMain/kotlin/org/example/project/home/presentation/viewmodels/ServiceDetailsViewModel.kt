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
import org.example.project.home.domain.repository.ServiceDetailsRepository

@Immutable
data class ServiceDetailsUiState(
    val isLoading: Boolean = false,
    val serviceDetails: ServiceDetails? = null,
    val selectedCategory: CategoryItem? = null,
    val filteredSections: List<ServiceSection> = emptyList(),
    val errorMessage: String? = null
)

sealed interface ServiceDetailsEvent {
    data class LoadService(val serviceId: Long) : ServiceDetailsEvent
    data class CategorySelected(val categoryId: String) : ServiceDetailsEvent
    data class SubServiceClicked(val subServiceId: String) : ServiceDetailsEvent
    data object BackClicked : ServiceDetailsEvent
    data object Retry : ServiceDetailsEvent
    data object ErrorDismissed : ServiceDetailsEvent
}

sealed interface ServiceDetailsEffect {
    data object NavigateBack : ServiceDetailsEffect
    data class NavigateToSubServiceDetails(val subServiceId: String) : ServiceDetailsEffect
    data class ShowMessage(val message: String) : ServiceDetailsEffect
}

class ServiceDetailsViewModel(
    private val repository: ServiceDetailsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceDetailsUiState())
    val state: StateFlow<ServiceDetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ServiceDetailsEffect>()
    val effect: SharedFlow<ServiceDetailsEffect> = _effect.asSharedFlow()

    fun onEvent(event: ServiceDetailsEvent) {
        when (event) {
            is ServiceDetailsEvent.LoadService -> loadServiceDetails(event.serviceId)
            is ServiceDetailsEvent.CategorySelected -> { /* No longer needed - dropdown opening handled in UI */ }
            is ServiceDetailsEvent.SubServiceClicked -> viewModelScope.launch {
                _effect.emit(ServiceDetailsEffect.NavigateToSubServiceDetails(event.subServiceId))
            }
            ServiceDetailsEvent.BackClicked -> viewModelScope.launch {
                _effect.emit(ServiceDetailsEffect.NavigateBack)
            }
            ServiceDetailsEvent.Retry -> {
                clearError()
                _state.value.serviceDetails?.id?.let { loadServiceDetails(it) }
            }
            ServiceDetailsEvent.ErrorDismissed -> clearError()
        }
    }

    private fun setLoading(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }

    private fun setError(message: String?) {
        _state.update { it.copy(errorMessage = message) }
    }

    private fun clearError() = setError(null)

    private fun loadServiceDetails(serviceId: Long) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val result = repository.getServiceDetails(serviceId)
                result.onSuccess { details ->
                    _state.update {
                        it.copy(
                            serviceDetails = details,
                            filteredSections = details.sections,
                            selectedCategory = null
                        )
                    }
                    log("servicedetails", "det$details")
                }.onFailure { error ->
                    setError("Failed to load service details: ${error.message}")
                    log("servicedetails", error.message.toString())
                }
            } catch (e: Exception) {
                setError("Error loading service details: ${e.message}")
                log("servicedetails", e.message.toString())
            } finally {
                setLoading(false)
            }
        }
    }

    private fun filterByCategory(categoryId: String) {
        val currentDetails = _state.value.serviceDetails ?: return
        val selectedCategory = currentDetails.categories.find { it.id == categoryId }

        val filteredSections = currentDetails.sections.filter { it.id == categoryId }

        _state.update {
            it.copy(
                selectedCategory = selectedCategory,
                filteredSections = filteredSections.ifEmpty { currentDetails.sections }
            )
        }
    }
}
