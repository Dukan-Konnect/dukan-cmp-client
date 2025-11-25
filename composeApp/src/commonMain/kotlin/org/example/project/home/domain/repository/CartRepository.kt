package org.example.project.home.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.home.domain.model.CartItem
import org.example.project.home.domain.model.CartSummary
import org.example.project.home.domain.model.CartTotals
import org.example.project.home.domain.model.CartData

interface CartRepository {
    // Observables for reactive UI
    fun observeCartItems(): Flow<List<CartItem>>
    fun observeCartSummary(): Flow<CartSummary?>
    fun observeCartTotals(): Flow<CartTotals?>
    fun observeCartData(): Flow<CartData>

    // Item management
    suspend fun upsertItem(item: CartItem): Result<Unit>
    suspend fun addItem(item: CartItem): Result<Unit>
    suspend fun updateItemQuantity(productId: String, quantity: Int): Result<Unit>
    suspend fun removeItem(productId: String): Result<Unit>
    suspend fun clearAllItems(): Result<Unit>
    suspend fun getCartItems(): Result<List<CartItem>>

    // Summary management
    suspend fun updatePhoneNumber(phoneNumber: String): Result<Unit>
    suspend fun updateAddress(address: String?): Result<Unit>
    suspend fun updateTimeSlot(timeSlot: String?): Result<Unit>
    suspend fun updateTaxPercent(taxPercent: Double): Result<Unit>
    suspend fun updateDeliveryCharges(deliveryChargesCents: Long): Result<Unit>
    suspend fun getCartSummary(): Result<CartSummary?>

    // User management
    suspend fun updateUserName(name: String?): Result<Unit>
    suspend fun updateUserLocation(address: String): Result<Unit>

    // Totals calculation
    suspend fun calculateTotals(): Result<CartTotals>

    // Bulk operations
    suspend fun initializeCart(phoneNumber: String): Result<Unit>
    suspend fun clearCart(): Result<Unit>
}
