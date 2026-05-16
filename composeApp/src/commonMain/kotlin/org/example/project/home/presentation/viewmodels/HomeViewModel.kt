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
    val dialogState: DialogState? = null, // <-- Replaced isLoading with DialogState
    val isSearchEnabled: Boolean = false,
    val userLocation: String? = "Unable to fetch location",
    val banner: Banner? = null,
    val personalService: List<Service> = emptyList(),
    val homeService: List<Service> = emptyList(),
    val trendingService: List<Service> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<Service> = emptyList()
) {
    // <-- Added DialogState exactly like Mifos
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

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
    // Removed ShowMessage since errors are now handled by DialogState
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
            // Set loading state and disable search
            _uiState.update {
                it.copy(
                    dialogState = HomeUiState.DialogState.Loading,
                    isSearchEnabled = false
                )
            }

            // Fetch data concurrently or sequentially
            val personalResult = homeRepository.getPersonalServices()
            val homeResult = homeRepository.getHomeServices()
            val trendingResult = homeRepository.getTrendingServices()

            // Check for any errors across the network requests
            if (personalResult is DataState.Error || homeResult is DataState.Error || trendingResult is DataState.Error) {
                // If any fail, show the error state
                val errorMsg = (personalResult as? DataState.Error)?.exception?.message
                    ?: (homeResult as? DataState.Error)?.exception?.message
                    ?: (trendingResult as? DataState.Error)?.exception?.message
                    ?: "An unexpected error occurred"

                _uiState.update {
                    it.copy(dialogState = HomeUiState.DialogState.Error("Failed to load services: $errorMsg"))
                }
                return@launch
            }

            // If all succeed, clear the dialog state and populate data
            _uiState.update {
                it.copy(
                    personalService = (personalResult as DataState.Success).data,
                    homeService = (homeResult as DataState.Success).data,
                    trendingService = (trendingResult as DataState.Success).data,
                    dialogState = null, // Clears the loading/error dialog
                    isSearchEnabled = true
                )
            }
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

    private fun emitEffect(effect: HomeEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}