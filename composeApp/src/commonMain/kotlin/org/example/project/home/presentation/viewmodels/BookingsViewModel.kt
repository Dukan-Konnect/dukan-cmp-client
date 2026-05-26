package org.example.project.home.presentation.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.core.utils.DataState
import org.example.project.home.domain.model.Booking
import org.example.project.home.domain.repository.BookingRepository
import org.example.project.home.domain.repository.BookingRemoteRepository

@Immutable
data class BookingsUiState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class BookingsViewModel(
    private val bookingRepository: BookingRepository,
    private val bookingRemoteRepository: BookingRemoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookingsUiState())
    val state: StateFlow<BookingsUiState> = _state.asStateFlow()

    init {
        loadBookings()
        refreshFromBackend()
    }

    private fun loadBookings() {
        viewModelScope.launch {
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

    private fun refreshFromBackend() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val remoteState = bookingRemoteRepository.getMyBookings()) {
                is DataState.Success -> {
                    remoteState.data.forEach { booking ->
                        bookingRepository.createBooking(booking)
                    }
                    _state.update { it.copy(isLoading = false, errorMessage = null) }
                }

                is DataState.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = remoteState.message) }
                }

                DataState.Loading -> Unit
            }
        }
    }
}
