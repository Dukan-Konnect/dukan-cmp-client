# 🚀 Quick Start Guide: Using SummaryViewModel

## ✅ Everything is Ready!

All Dependency Injection (DI) is configured. You can now use the `SummaryViewModel` in your screens.

---

## 📱 HOW TO USE IN YOUR SCREEN

### 1. **In Your SummaryScreen Composable**

```kotlin
import org.example.project.home.presentation.viewmodels.SummaryViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel = koinViewModel() // ← THIS IS THE INSTANCE!
) {
    val uiState by viewModel.state.collectAsState()
    
    // Access data
    val items = uiState.cartItems
    val summary = uiState.cartSummary
    val totals = uiState.cartTotals
    
    // Send events
    Button(onClick = { 
        viewModel.onEvent(SummaryEvent.UpdateItemQuantity(123L, 2)) 
    }) {
        Text("Update Quantity")
    }
}
```

---

## 🎯 AVAILABLE FUNCTIONS

### **A. Get Cart Data (from uiState)**

```kotlin
val uiState by viewModel.state.collectAsState()

// Cart Items
uiState.cartItems              // List of all items in cart
uiState.cartSummary            // Phone, address, time slot
uiState.cartTotals             // Item total, taxes, delivery, total
uiState.isCartEmpty            // true/false
uiState.isLoading              // Loading indicator
uiState.errorMessage           // Error message if any
```

### **B. Update Item Quantity**

```kotlin
// Update to specific quantity
viewModel.onEvent(
    SummaryEvent.UpdateItemQuantity(
        productId = 123L, 
        quantity = 3
    )
)

// OR use convenience methods
viewModel.incrementQuantity(productId = 123L)  // +1
viewModel.decrementQuantity(productId = 123L)  // -1
```

### **C. Remove Item from Cart**

```kotlin
viewModel.onEvent(SummaryEvent.RemoveItem(productId = 123L))
```

### **D. Update User Information**

```kotlin
// Update phone number
viewModel.onEvent(SummaryEvent.UpdatePhoneNumber("9876543210"))

// Update address
viewModel.onEvent(SummaryEvent.UpdateAddress("123 Main St, City"))

// Update time slot
viewModel.onEvent(SummaryEvent.UpdateTimeSlot("10:00 AM - 12:00 PM"))
```

### **E. Clear Entire Cart**

```kotlin
viewModel.onEvent(SummaryEvent.ClearCart)
```

### **F. Proceed to Payment**

```kotlin
viewModel.onEvent(SummaryEvent.ProceedToPayment)
```

### **G. Navigate Back**

```kotlin
viewModel.onEvent(SummaryEvent.BackClicked)
```

### **H. Format Prices**

```kotlin
// Format 499900 cents → "₹4999"
val formattedPrice = viewModel.formatPrice(499900)

// Format 499950 cents → "₹4999.50"  
val formattedPriceWithDecimals = viewModel.formatPriceWithDecimals(499950)
```

---

## 🔔 HANDLE NAVIGATION & MESSAGES

```kotlin
LaunchedEffect(viewModel) {
    viewModel.effect.collect { effect ->
        when (effect) {
            SummaryEffect.NavigateBack -> {
                navController.popBackStack()
            }
            SummaryEffect.NavigateToPayment -> {
                navController.navigate(PaymentRoute)
            }
            is SummaryEffect.ShowMessage -> {
                snackbarHostState.showSnackbar(effect.message)
            }
        }
    }
}
```

---

## 💾 INITIALIZE CART (After Login)

In your login/auth screen, initialize the cart with user's phone number:

```kotlin
val cartUseCases: CartUseCases by inject()

viewModelScope.launch {
    cartUseCases.initializeCart(phoneNumber = "9876543210")
}
```

This creates the cart in the database and it will persist across app restarts!

---

## 📦 ADDING ITEMS TO CART

From ServiceDetailScreen or any other screen:

```kotlin
val cartUseCases: CartUseCases by inject()

viewModelScope.launch {
    cartUseCases.addOrUpdateItem(
        productId = 123L,
        name = "Classic Haircut",
        priceCents = 49900L, // ₹499.00
        imageUrl = "https://example.com/image.jpg"
    )
}
```

---

## ✨ COMPLETE EXAMPLE

```kotlin
@Composable
fun SummaryScreen(
    onBack: () -> Unit = {},
    onNavigateToPayment: () -> Unit = {},
    viewModel: SummaryViewModel = koinViewModel() // ← THE INSTANCE!
) {
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

    Column {
        // Show items
        uiState.cartItems.forEach { item ->
            Row {
                Text(item.name)
                Text(viewModel.formatPrice(item.priceCents))
                
                IconButton(onClick = { 
                    viewModel.decrementQuantity(item.productId) 
                }) {
                    Icon(Icons.Default.Remove, "Decrease")
                }
                
                Text("${item.quantity}")
                
                IconButton(onClick = { 
                    viewModel.incrementQuantity(item.productId) 
                }) {
                    Icon(Icons.Default.Add, "Increase")
                }
            }
        }

        // Show totals
        uiState.cartTotals?.let { totals ->
            Text("Item Total: ${viewModel.formatPrice(totals.itemTotalCents)}")
            Text("Taxes: ${viewModel.formatPrice(totals.taxesCents)}")
            Text("Total: ${viewModel.formatPrice(totals.totalCents)}")
        }

        // Payment button
        Button(
            onClick = { viewModel.onEvent(SummaryEvent.ProceedToPayment) }
        ) {
            Text("Proceed to Payment")
        }
    }
}
```

---

## 🎉 That's It!

**The instance you need is**: `viewModel: SummaryViewModel = koinViewModel()`

All the database, repository, and use cases are automatically injected via Koin DI.

**No additional setup required!** Just use `koinViewModel()` and start building your UI! 🚀
