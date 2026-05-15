package org.example.project.home.presentation.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.data.repository.HomeRepository
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.model.home.Banner
import org.example.project.core.model.home.Service
import org.example.project.core.utils.DataState

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val isSearchEnabled: Boolean = false, // <-- Added to control TextField state
    val userLocation: String? = "Unable to fetch location",
    val banner: Banner? = null,
    val personalService: List<Service> = emptyList(),
    val homeService: List<Service> = emptyList(),
    val trendingService: List<Service> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<Service> = emptyList() // <-- Added to hold filtered results
)

sealed interface HomeIntent {
    data object ScreenStarted : HomeIntent
    data object LocationClicked : HomeIntent
    data class SearchQueryChanged(val query: String) : HomeIntent
    data object BannerClicked : HomeIntent
    data class ServiceClicked(val id: Int) : HomeIntent
    data object Retry : HomeIntent
}

sealed interface HomeEffect {
    data class NavigateToService(val id: Int) : HomeEffect
    data object OpenLocationPicker : HomeEffect
    data object OpenBanner : HomeEffect
    data class ShowMessage(val message: String) : HomeEffect
}

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val prefRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()

    init {
        observeUserLocation()
        handleIntent(HomeIntent.ScreenStarted)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.ScreenStarted -> loadAllData()
            HomeIntent.LocationClicked -> emitEffect(HomeEffect.OpenLocationPicker)
            is HomeIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            HomeIntent.BannerClicked -> emitEffect(HomeEffect.OpenBanner)
            is HomeIntent.ServiceClicked -> emitEffect(HomeEffect.NavigateToService(intent.id))
            HomeIntent.Retry -> loadAllData()
        }
    }

    private fun updateSearchQuery(query: String) {
        val allAvailableServices = (_uiState.value.personalService +
                _uiState.value.homeService +
                _uiState.value.trendingService).distinctBy { it.id }

        val results = if (query.isBlank()) {
            emptyList()
        } else {
            allAvailableServices.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        _uiState.update {
            it.copy(
                searchQuery = query,
                searchResults = results
            )
        }
    }

    private fun loadAllData() {
        viewModelScope.launch {
            showLoading()

            // Disable search while fresh data is loading
            _uiState.update { it.copy(isSearchEnabled = false) }

            // Fetch data
            loadPersonalServices()
            loadHomeServices()
            loadTrendingServices()

            // Enable search once everything is fully loaded
            _uiState.update { it.copy(isSearchEnabled = true) }
            hideLoading()
        }
    }

    private fun observeUserLocation() {
        viewModelScope.launch {
            prefRepository.userData.collectLatest { userData ->
                val displayAddress = userData.address.ifEmpty { "Unable to fetch location" }
                _uiState.update { it.copy(userLocation = displayAddress) }
            }
        }
    }

    private suspend fun loadPersonalServices() {
        when (val result = homeRepository.getPersonalServices()) {
            is DataState.Success -> {
                _uiState.update { it.copy(personalService = result.data) }
            }
            is DataState.Error -> {
                showError("Failed to load personal services: ${result.exception.message}")
            }
            DataState.Loading -> Unit
        }
    }

    private suspend fun loadHomeServices() {
        when (val result = homeRepository.getHomeServices()) {
            is DataState.Success -> {
                _uiState.update { it.copy(homeService = result.data) }
            }
            is DataState.Error -> {
                showError("Failed to load home services: ${result.exception.message}")
            }
            DataState.Loading -> Unit
        }
    }

    private suspend fun loadTrendingServices() {
        when (val result = homeRepository.getTrendingServices()) {
            is DataState.Success -> {
                _uiState.update { it.copy(trendingService = result.data) }
            }
            is DataState.Error -> {
                showError("Failed to load trending services: ${result.exception.message}")
            }
            DataState.Loading -> Unit
        }
    }

    private fun showLoading() {
        _uiState.update { it.copy(isLoading = true) }
    }

    private fun hideLoading() {
        _uiState.update { it.copy(isLoading = false) }
    }

    private fun showError(message: String) {
        viewModelScope.launch {
            _effect.emit(HomeEffect.ShowMessage(message))
        }
    }

    private fun emitEffect(effect: HomeEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}