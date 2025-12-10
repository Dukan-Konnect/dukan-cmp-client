package org.example.project.home.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.example.project.home.data.local.dao.CartDao
import org.example.project.home.data.local.entities.CartItemEntity
import org.example.project.home.data.local.entities.CartSummaryEntity
import org.example.project.home.data.local.mappers.toDomainModel
import org.example.project.home.data.local.mappers.toDomainModels
import org.example.project.home.data.local.mappers.toEntity
import org.example.project.home.data.local.util.getCurrentTimeMillis
import org.example.project.home.domain.model.CartData
import org.example.project.home.domain.model.CartItem
import org.example.project.home.domain.model.CartSummary
import org.example.project.home.domain.model.CartTotals
import org.example.project.home.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartDao: CartDao
) : CartRepository {

    override fun observeCartItems(): Flow<List<CartItem>> =
        cartDao.observeCartItems().map { it.toDomainModels() }

    override fun observeCartSummary(): Flow<CartSummary?> =
        cartDao.observeCartSummary().map { it?.toDomainModel() }

    override fun observeCartTotals() = combine(observeCartItems(), observeCartSummary()) { items, summary ->
        if (summary != null) CartSummary.calculateTotals(items, summary) else null
    }

    override fun observeCartData() = combine(observeCartItems(), observeCartSummary(), observeCartTotals()) { items, summary, totals ->
        CartData(items, summary, totals)
    }

    override suspend fun upsertItem(item: CartItem): Result<Unit> = runCatching {
        cartDao.upsertItem(item.toEntity())
    }

    override suspend fun addItem(item: CartItem): Result<Unit> = runCatching {
        cartDao.upsertItem(item.toEntity())
    }

    override suspend fun updateItemQuantity(productId: String, quantity: Int): Result<Unit> = runCatching {
        // Quantity is removed - if called, just remove the item
        if (quantity <= 0) {
            cartDao.deleteItem(productId)
        }
    }

    override suspend fun removeItem(productId: String): Result<Unit> = runCatching {
        cartDao.deleteItem(productId)
    }

    override suspend fun clearAllItems(): Result<Unit> = runCatching { cartDao.clearAllItems() }

    override suspend fun getCartItems() = runCatching { cartDao.getCartItems().toDomainModels() }

    override suspend fun updatePhoneNumber(phoneNumber: String) = runCatching { cartDao.updatePhoneNumber(phoneNumber, getCurrentTimeMillis()) }
    override suspend fun updateAddress(address: String?) = runCatching { cartDao.updateAddress(address, getCurrentTimeMillis()) }
    override suspend fun updateTimeSlot(timeSlot: String?) = runCatching { cartDao.updateTimeSlot(timeSlot, getCurrentTimeMillis()) }
    override suspend fun updateTaxPercent(taxPercent: Double) = runCatching { cartDao.updateTaxPercent(taxPercent, getCurrentTimeMillis()) }
    override suspend fun updateDeliveryCharges(deliveryChargesCents: Long) = runCatching { cartDao.updateDeliveryCharges(deliveryChargesCents, getCurrentTimeMillis()) }
    override suspend fun getCartSummary() = runCatching { cartDao.getCartSummary()?.toDomainModel() }
    override suspend fun calculateTotals() = runCatching {
        val items = cartDao.getCartItems().toDomainModels()
        val summary = cartDao.getCartSummary()?.toDomainModel() ?: error("Cart summary not initialized")
        CartSummary.calculateTotals(items, summary)
    }
    override suspend fun initializeCart(phoneNumber: String) = runCatching { cartDao.initializeCart(phoneNumber) }
    override suspend fun clearCart() = runCatching { cartDao.clearCart() }

    override suspend fun updateUserName(name: String?): Result<Unit> = runCatching {
        val existing = cartDao.getCartSummary()
        val now = getCurrentTimeMillis()
        if (existing == null) {
            cartDao.insertOrUpdateSummary(
                CartSummaryEntity(
                    name = name,
                    phoneNumber = "", // will be filled later
                    address = null,
                    timeSlot = null,
                    taxPercent = 5.0,
                    deliveryChargesCents = 0L,
                    createdAt = now,
                    updatedAt = now
                )
            )
        } else {
            cartDao.insertOrUpdateSummary(
                existing.copy(name = name, updatedAt = now)
            )
        }
    }

    override suspend fun updateUserLocation(address: String): Result<Unit> = updateAddress(address)
}
