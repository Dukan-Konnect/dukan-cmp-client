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
    data class LoadService(val serviceId: Int) : ServiceDetailsEvent
    data class CategorySelected(val categoryId: Int) : ServiceDetailsEvent
    data class SubServiceClicked(val subServiceId: Int) : ServiceDetailsEvent
    data object BackClicked : ServiceDetailsEvent
    data object Retry : ServiceDetailsEvent
    data object ErrorDismissed : ServiceDetailsEvent
}

sealed interface ServiceDetailsEffect {
    data object NavigateBack : ServiceDetailsEffect
    data class NavigateToSubServiceDetails(val subServiceId: Int) : ServiceDetailsEffect
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
            is ServiceDetailsEvent.CategorySelected -> filterByCategory(event.categoryId)
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

    private fun loadServiceDetails(serviceId: Int) {
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
                }.onFailure { error ->
                    setError("Failed to load service details: ${error.message}")
                }
            } catch (e: Exception) {
                setError("Error loading service details: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun filterByCategory(categoryId: Int) {
        val currentDetails = _state.value.serviceDetails ?: return
        val selectedCategory = currentDetails.categories.find { it.id == categoryId }

        // For now, just set the selected category
        // Later you can implement actual filtering logic based on category
        _state.update {
            it.copy(
                selectedCategory = selectedCategory,
                filteredSections = currentDetails.sections // TODO: Implement filtering logic
            )
        }
    }
}
