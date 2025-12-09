package org.example.project.home.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.project.home.domain.model.CartData
import org.example.project.home.domain.model.CartItem
import org.example.project.home.domain.model.CartSummary
import org.example.project.home.domain.model.CartTotals
import org.example.project.home.domain.repository.CartRepository

class CartUseCases(
    private val cartRepository: CartRepository
) {
    // Observables for reactive UI
    fun observeCartData(): Flow<CartData> = cartRepository.observeCartData()
    fun observeCartItems(): Flow<List<CartItem>> = cartRepository.observeCartItems()
    fun observeCartSummary(): Flow<CartSummary?> = cartRepository.observeCartSummary()
    fun observeCartTotals(): Flow<CartTotals?> = cartRepository.observeCartTotals()

    // Item operations
    suspend fun addItemToCart(item: CartItem): Result<Unit> {
        return cartRepository.addItem(item)
    }

    suspend fun updateItemQuantity(productId: String, quantity: Int): Result<Unit> {
        return cartRepository.updateItemQuantity(productId, quantity)
    }

    suspend fun removeItemFromCart(productId: String): Result<Unit> {
        return cartRepository.removeItem(productId)
    }

    suspend fun clearCartItems(): Result<Unit> {
        return cartRepository.clearAllItems()
    }

    // Cart summary operations
    suspend fun updatePhoneNumber(phoneNumber: String): Result<Unit> {
        return cartRepository.updatePhoneNumber(phoneNumber)
    }

    suspend fun updateDeliveryAddress(address: String?): Result<Unit> {
        return cartRepository.updateAddress(address)
    }

    suspend fun updateTimeSlot(timeSlot: String?): Result<Unit> {
        return cartRepository.updateTimeSlot(timeSlot)
    }

    suspend fun updateTaxPercent(taxPercent: Double): Result<Unit> {
        return cartRepository.updateTaxPercent(taxPercent)
    }

    suspend fun updateDeliveryCharges(deliveryChargesCents: Long): Result<Unit> {
        return cartRepository.updateDeliveryCharges(deliveryChargesCents)
    }

    // Cart management
    suspend fun initializeCart(phoneNumber: String): Result<Unit> {
        return cartRepository.initializeCart(phoneNumber)
    }

    suspend fun clearEntireCart(): Result<Unit> {
        return cartRepository.clearCart()
    }


    suspend fun updateUserName(name: String?): Result<Unit> = cartRepository.updateUserName(name)

    suspend fun updateUserInfo(name: String, phoneNumber: String): Result<Unit> {
        val nameResult = cartRepository.updateUserName(name)
        if (nameResult.isFailure) return nameResult

        val phoneResult = cartRepository.updatePhoneNumber(phoneNumber)
        return phoneResult
    }
}
