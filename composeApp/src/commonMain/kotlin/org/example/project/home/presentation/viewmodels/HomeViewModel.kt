package org.example.project.home.presentation.viewmodels

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
import kotlinx.coroutines.yield
import androidx.compose.runtime.Immutable
import org.example.project.core.log
import org.example.project.home.domain.model.Banner
import org.example.project.home.domain.model.Service
import org.example.project.home.domain.model.UserLocation
import org.example.project.home.domain.repository.HomeRepository

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val userLocation: UserLocation? = null,
    val banner: Banner? = null,
    val personalServices: List<Service> = emptyList(),
    val homeServices: List<Service> = emptyList(),
    val trendingServices: List<Service> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

sealed interface HomeEvent {
    data object ScreenStarted : HomeEvent
    data object LocationClicked : HomeEvent
    data class SearchQueryChanged(val query: String) : HomeEvent
    data object BannerClicked : HomeEvent
    data class ServiceClicked(val id: Int) : HomeEvent
    data object Retry : HomeEvent
    data object ErrorDismissed : HomeEvent
}

sealed interface HomeEffect {
    data class NavigateToService(val id: Int) : HomeEffect
    data object OpenLocationPicker : HomeEffect
    data object OpenBanner : HomeEffect
    data class ShowMessage(val message: String) : HomeEffect
}

class HomeViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {

    // Single source of truth for UI state (MVI)
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    // One-off events (navigation, toasts)
    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()

    init {
        onEvent(HomeEvent.ScreenStarted)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.ScreenStarted -> loadAllData()
            HomeEvent.LocationClicked -> viewModelScope.launch { _effect.emit(HomeEffect.OpenLocationPicker) }
            is HomeEvent.SearchQueryChanged -> _state.update { it.copy(searchQuery = event.query) }
            HomeEvent.BannerClicked -> viewModelScope.launch { _effect.emit(HomeEffect.OpenBanner) }
            is HomeEvent.ServiceClicked -> viewModelScope.launch { _effect.emit(HomeEffect.NavigateToService(event.id)) }
            HomeEvent.Retry -> {
                clearError()
                loadAllData()
            }
            HomeEvent.ErrorDismissed -> clearError()

        }
    }

    private fun setLoading(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }

    private fun setError(message: String?) {
        _state.update { it.copy(errorMessage = message) }
    }

    private fun clearError() = setError(null)

    private fun loadAllData() {
        viewModelScope.launch {
            yield()
            setLoading(true)
            try {
                // Launch sequentially to keep flow similar to previous behavior.
                loadUserLocation()
                loadBanner()
                loadPersonalServices()
                loadHomeServices()
                loadTrendingServices()
            } finally {
                setLoading(false)
            }
        }
    }

    private suspend fun loadUserLocation() {
        try {
            val result = homeRepository.getUserLocation()
            result.onSuccess { location ->
                _state.update { it.copy(userLocation = location) }
            }.onFailure { error ->
                setError("Failed to load location: ${'$'}{error.message}")
            }
        } catch (e: Exception) {
            setError("Error loading location: ${'$'}{e.message}")
        }
    }

    private suspend fun loadBanner() {
        try {
            val result = homeRepository.getBanner()
            result.onSuccess { banner ->
                _state.update { it.copy(banner = banner) }
            }.onFailure { error ->
                setError("Failed to load banner: ${'$'}{error.message}")
            }
        } catch (e: Exception) {
            setError("Error loading banner: ${'$'}{e.message}")
        }
    }

    private suspend fun loadPersonalServices() {
        try {
            val result = homeRepository.getPersonalServices()
            result.onSuccess { services ->
                _state.update { it.copy(personalServices = services) }
                log("homeviewmodel", "personal services: ${'$'}{services.size}")
            }.onFailure { error ->
                setError("Failed to load personal services: ${'$'}{error.message}")
                log("homeviewmodel", "ee : ${'$'}{error.message}")
            }
        } catch (e: Exception) {
            setError("Error loading personal services: ${'$'}{e.message}")
            log("homeviewmodel", "ee : ${'$'}{e.message}")
        }
    }

    private suspend fun loadHomeServices() {
        try {
            val result = homeRepository.getHomeServices()
            result.onSuccess { services ->
                _state.update { it.copy(homeServices = services) }
            }.onFailure { error ->
                setError("Failed to load home services: ${'$'}{error.message}")
            }
        } catch (e: Exception) {
            setError("Error loading home services: ${'$'}{e.message}")
        }
    }

    private suspend fun loadTrendingServices() {
        try {
            val result = homeRepository.getTrendingServices()
            result.onSuccess { services ->
                _state.update { it.copy(trendingServices = services) }
            }.onFailure { error ->
                setError("Failed to load trending services: ${'$'}{error.message}")
            }
        } catch (e: Exception) {
            setError("Error loading trending services: ${'$'}{e.message}")
        }
    }

    // Optional: expose explicit commands for unit tests or imperative triggers
    fun refreshAll() = onEvent(HomeEvent.Retry)
}
