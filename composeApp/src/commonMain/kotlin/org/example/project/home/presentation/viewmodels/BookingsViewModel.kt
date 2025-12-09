package org.example.project.home.presentation.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.home.domain.model.Booking
import org.example.project.home.domain.repository.BookingRepository

@Immutable
data class BookingsUiState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class BookingsViewModel(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookingsUiState())
    val state: StateFlow<BookingsUiState> = _state.asStateFlow()

    init {
        loadBookings()
    }

    private fun loadBookings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            bookingRepository.observeAllBookings()
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load bookings: ${error.message}"
                        )
                    }
                }
                .collect { bookings ->
                    _state.update {
                        it.copy(
                            bookings = bookings,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }
}

