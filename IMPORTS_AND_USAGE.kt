// ════════════════════════════════════════════════════════════════════════════
// EXACT IMPORTS NEEDED FOR USING SUMMARYVIEWMODEL
// ════════════════════════════════════════════════════════════════════════════

// 1. IMPORT THE VIEWMODEL
import org.example.project.home.presentation.viewmodels.SummaryViewModel

// 2. IMPORT KOIN VIEWMODEL FUNCTION
import org.koin.compose.viewmodel.koinViewModel

// 3. IMPORT FOR COLLECTING STATE
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

// 4. IMPORT FOR HANDLING EFFECTS
import androidx.compose.runtime.LaunchedEffect

// 5. IMPORT EVENTS & EFFECTS (if you use them directly)
import org.example.project.home.presentation.viewmodels.SummaryEvent
import org.example.project.home.presentation.viewmodels.SummaryEffect

// 6. IMPORT CART MODELS (if you need to access them)
import org.example.project.home.domain.model.CartItem
import org.example.project.home.domain.model.CartSummary
import org.example.project.home.domain.model.CartTotals

// ════════════════════════════════════════════════════════════════════════════
// MINIMAL EXAMPLE WITH ALL IMPORTS
// ════════════════════════════════════════════════════════════════════════════

package org.example.project.home.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.example.project.home.presentation.viewmodels.SummaryViewModel
import org.example.project.home.presentation.viewmodels.SummaryEvent
import org.example.project.home.presentation.viewmodels.SummaryEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SummaryScreen(
    onBack: () -> Unit = {},
    onNavigateToPayment: () -> Unit = {},
    viewModel: SummaryViewModel = koinViewModel() // ← THE INSTANCE
) {
    // Collect state
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle effects
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SummaryEffect.NavigateBack -> onBack()
                SummaryEffect.NavigateToPayment -> onNavigateToPayment()
                is SummaryEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // Your UI here
    Column {
        Text("Cart has ${uiState.cartItems.size} items")

        Button(onClick = {
            viewModel.onEvent(SummaryEvent.ProceedToPayment)
        }) {
            Text("Pay Now")
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// FOR INITIALIZING CART (In Login/Auth Screen)
// ════════════════════════════════════════════════════════════════════════════

import org.example.project.home.domain.usecase.CartUseCases
import org.koin.compose.koinInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel
) {
    // Inject CartUseCases
    val cartUseCases: CartUseCases = koinInject()

    // After successful login
    val onLoginSuccess = { phoneNumber: String ->
        authViewModel.viewModelScope.launch {
            cartUseCases.initializeCart(phoneNumber)
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// FOR ADDING ITEMS TO CART (In ServiceDetail or Product Screens)
// ════════════════════════════════════════════════════════════════════════════

import org.example.project.home.domain.usecase.CartUseCases
import org.koin.compose.koinInject

@Composable
fun ProductDetailScreen() {
    val cartUseCases: CartUseCases = koinInject()

    Button(onClick = {
        // Add to cart
        viewModelScope.launch {
            cartUseCases.addOrUpdateItem(
                productId = 123L,
                name = "Product Name",
                priceCents = 49900L,
                imageUrl = "https://..."
            )
        }
    }) {
        Text("Add to Cart")
    }
}

// ════════════════════════════════════════════════════════════════════════════
// THAT'S ALL YOU NEED!
// ════════════════════════════════════════════════════════════════════════════
