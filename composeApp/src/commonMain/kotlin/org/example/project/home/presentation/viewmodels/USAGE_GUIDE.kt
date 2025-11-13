package org.example.project.home.presentation.viewmodels

/**
 * USAGE GUIDE FOR SUMMARYVIEWMODEL
 *
 * The SummaryViewModel is fully configured and ready to use with Koin DI.
 *
 * ============================================================================
 * HOW TO USE IN YOUR COMPOSABLES
 * ============================================================================
 *
 * 1. INJECT THE VIEWMODEL IN YOUR COMPOSABLE:
 *
 * ```kotlin
 * @Composable
 * fun SummaryScreen(
 *     viewModel: SummaryViewModel = koinViewModel()
 * ) {
 *     val uiState by viewModel.state.collectAsState()
 *
 *     // Your UI code here
 * }
 * ```
 *
 * 2. ACCESS CART DATA FROM UI STATE:
 *
 * ```kotlin
 * val cartItems = uiState.cartItems          // List<CartItem>
 * val cartSummary = uiState.cartSummary      // CartSummary? (phone, address, timeSlot)
 * val cartTotals = uiState.cartTotals        // CartTotals? (itemTotal, taxes, total)
 * val isLoading = uiState.isLoading          // Boolean
 * val isCartEmpty = uiState.isCartEmpty      // Boolean
 * val errorMessage = uiState.errorMessage    // String?
 * ```
 *
 * 3. HANDLE USER ACTIONS BY SENDING EVENTS:
 *
 * ```kotlin
 * // Update item quantity
 * viewModel.onEvent(SummaryEvent.UpdateItemQuantity(productId = 123L, quantity = 2))
 *
 * // Remove item from cart
 * viewModel.onEvent(SummaryEvent.RemoveItem(productId = 123L))
 *
 * // Update phone number
 * viewModel.onEvent(SummaryEvent.UpdatePhoneNumber("9876543210"))
 *
 * // Update address
 * viewModel.onEvent(SummaryEvent.UpdateAddress("123 Main St, City"))
 *
 * // Update time slot
 * viewModel.onEvent(SummaryEvent.UpdateTimeSlot("10:00 AM - 12:00 PM"))
 *
 * // Clear cart
 * viewModel.onEvent(SummaryEvent.ClearCart)
 *
 * // Proceed to payment
 * viewModel.onEvent(SummaryEvent.ProceedToPayment)
 *
 * // Navigate back
 * viewModel.onEvent(SummaryEvent.BackClicked)
 * ```
 *
 * 4. LISTEN TO ONE-TIME EFFECTS:
 *
 * ```kotlin
 * LaunchedEffect(viewModel) {
 *     viewModel.effect.collect { effect ->
 *         when (effect) {
 *             SummaryEffect.NavigateBack -> {
 *                 navController.popBackStack()
 *             }
 *             SummaryEffect.NavigateToPayment -> {
 *                 navController.navigate(PaymentRoute)
 *             }
 *             is SummaryEffect.ShowMessage -> {
 *                 snackbarHostState.showSnackbar(effect.message)
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * 5. USE CONVENIENCE METHODS FOR QUANTITY CHANGES:
 *
 * ```kotlin
 * // Increment quantity by 1
 * viewModel.incrementQuantity(productId = 123L)
 *
 * // Decrement quantity by 1 (removes item if quantity becomes 0)
 * viewModel.decrementQuantity(productId = 123L)
 *
 * // Format price for display
 * val price = viewModel.formatPrice(499900) // Returns "₹4999"
 * val priceWithDecimals = viewModel.formatPriceWithDecimals(499950) // Returns "₹4999.50"
 * ```
 *
 * ============================================================================
 * COMPLETE EXAMPLE SCREEN
 * ============================================================================
 *
 * ```kotlin
 * @Composable
 * fun SummaryScreen(
 *     onBack: () -> Unit = {},
 *     onNavigateToPayment: () -> Unit = {},
 *     viewModel: SummaryViewModel = koinViewModel()
 * ) {
 *     val uiState by viewModel.state.collectAsState()
 *     val snackbarHostState = remember { SnackbarHostState() }
 *
 *     // Handle effects
 *     LaunchedEffect(viewModel) {
 *         viewModel.effect.collect { effect ->
 *             when (effect) {
 *                 SummaryEffect.NavigateBack -> onBack()
 *                 SummaryEffect.NavigateToPayment -> onNavigateToPayment()
 *                 is SummaryEffect.ShowMessage -> {
 *                     snackbarHostState.showSnackbar(effect.message)
 *                 }
 *             }
 *         }
 *     }
 *
 *     Scaffold(
 *         snackbarHost = { SnackbarHost(snackbarHostState) },
 *         topBar = {
 *             TopAppBar(
 *                 title = { Text("Cart Summary") },
 *                 navigationIcon = {
 *                     IconButton(onClick = { viewModel.onEvent(SummaryEvent.BackClicked) }) {
 *                         Icon(Icons.Default.ArrowBack, "Back")
 *                     }
 *                 }
 *             )
 *         }
 *     ) { padding ->
 *         Column(
 *             modifier = Modifier
 *                 .fillMaxSize()
 *                 .padding(padding)
 *         ) {
 *             if (uiState.isLoading) {
 *                 CircularProgressIndicator()
 *             }
 *
 *             if (uiState.isCartEmpty) {
 *                 Text("Your cart is empty")
 *             } else {
 *                 // Cart Items
 *                 LazyColumn(modifier = Modifier.weight(1f)) {
 *                     items(uiState.cartItems) { item ->
 *                         CartItemRow(
 *                             item = item,
 *                             onIncrement = { viewModel.incrementQuantity(item.productId) },
 *                             onDecrement = { viewModel.decrementQuantity(item.productId) },
 *                             onRemove = {
 *                                 viewModel.onEvent(SummaryEvent.RemoveItem(item.productId))
 *                             }
 *                         )
 *                     }
 *                 }
 *
 *                 // Totals Section
 *                 uiState.cartTotals?.let { totals ->
 *                     Column(modifier = Modifier.padding(16.dp)) {
 *                         Text("Item Total: ${viewModel.formatPrice(totals.itemTotalCents)}")
 *                         Text("Taxes: ${viewModel.formatPrice(totals.taxesCents)}")
 *                         Text("Delivery: ${viewModel.formatPrice(totals.deliveryChargesCents)}")
 *                         Divider()
 *                         Text(
 *                             "Total: ${viewModel.formatPrice(totals.totalCents)}",
 *                             fontWeight = FontWeight.Bold
 *                         )
 *                     }
 *                 }
 *
 *                 // Payment Button
 *                 Button(
 *                     onClick = { viewModel.onEvent(SummaryEvent.ProceedToPayment) },
 *                     modifier = Modifier
 *                         .fillMaxWidth()
 *                         .padding(16.dp)
 *                 ) {
 *                     Text("Proceed to Payment")
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ============================================================================
 * DEPENDENCY INJECTION SETUP (ALREADY CONFIGURED)
 * ============================================================================
 *
 * The following modules are already configured:
 *
 * 1. CartModule (cartModule):
 *    - CartDatabase (Room database)
 *    - CartDao (database access)
 *    - CartRepository (data layer)
 *    - CartUseCases (business logic)
 *
 * 2. HomeModule (homeModule):
 *    - SummaryViewModel (presentation layer)
 *
 * 3. AppModule (appModules):
 *    - Includes all modules
 *
 * Everything is automatically injected via Koin DI when you use:
 * `viewModel: SummaryViewModel = koinViewModel()`
 *
 * ============================================================================
 * INITIALIZATION
 * ============================================================================
 *
 * To initialize cart with user's phone number after login:
 *
 * ```kotlin
 * // In your auth/login screen or app initialization
 * val cartUseCases: CartUseCases by inject()
 *
 * viewModelScope.launch {
 *     cartUseCases.initializeCart(userPhoneNumber = "9876543210")
 * }
 * ```
 *
 * This creates the cart summary entry in database with the phone number.
 * The cart will persist across app restarts.
 *
 * ============================================================================
 * DATA FLOW
 * ============================================================================
 *
 * UI (Composable)
 *   ↓ sends events
 * SummaryViewModel
 *   ↓ calls use cases
 * CartUseCases
 *   ↓ calls repository
 * CartRepository
 *   ↓ accesses database
 * CartDao / Room Database
 *   ↓ emits Flow updates
 * UI (automatically recomposes)
 *
 * The Flow-based architecture ensures your UI is always in sync with the
 * database. Any changes (from any screen) automatically update all observers.
 */
