package org.example.project.home.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.example.project.home.data.local.dao.CartDao
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

    override fun observeCartItems(): Flow<List<CartItem>> {
        return cartDao.observeCartItems().map { entities ->
            entities.toDomainModels()
        }
    }

    override fun observeCartSummary(): Flow<CartSummary?> {
        return cartDao.observeCartSummary().map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun observeCartTotals(): Flow<CartTotals?> {
        return combine(
            observeCartItems(),
            observeCartSummary()
        ) { items, summary ->
            if (summary != null) {
                CartSummary.calculateTotals(items, summary)
            } else null
        }
    }

    override fun observeCartData(): Flow<CartData> {
        return combine(
            observeCartItems(),
            observeCartSummary(),
            observeCartTotals()
        ) { items, summary, totals ->
            CartData(
                items = items,
                summary = summary,
                totals = totals
            )
        }
    }

    override suspend fun addItem(item: CartItem): Result<Unit> {
        return try {
            val existingItem = cartDao.getCartItem(item.productId)
            if (existingItem != null) {
                // Update existing item quantity
                val newQuantity = existingItem.quantity + item.quantity
                cartDao.updateItemQuantity(item.productId, newQuantity, getCurrentTimeMillis())
            } else {
                // Insert new item
                cartDao.insertOrUpdateItem(item.toEntity())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateItemQuantity(productId: Long, quantity: Int): Result<Unit> {
        return try {
            if (quantity <= 0) {
                cartDao.deleteItem(productId)
            } else {
                val existingItem = cartDao.getCartItem(productId)
                if (existingItem != null) {
                    cartDao.updateItemQuantity(productId, quantity, getCurrentTimeMillis())
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeItem(productId: Long): Result<Unit> {
        return try {
            cartDao.deleteItem(productId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearAllItems(): Result<Unit> {
        return try {
            cartDao.clearAllItems()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartItems(): Result<List<CartItem>> {
        return try {
            val items = cartDao.getCartItems().toDomainModels()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePhoneNumber(phoneNumber: String): Result<Unit> {
        return try {
            cartDao.updatePhoneNumber(phoneNumber, getCurrentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAddress(address: String?): Result<Unit> {
        return try {
            cartDao.updateAddress(address, getCurrentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTimeSlot(timeSlot: String?): Result<Unit> {
        return try {
            cartDao.updateTimeSlot(timeSlot, getCurrentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTaxPercent(taxPercent: Double): Result<Unit> {
        return try {
            cartDao.updateTaxPercent(taxPercent, getCurrentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDeliveryCharges(deliveryChargesCents: Long): Result<Unit> {
        return try {
            cartDao.updateDeliveryCharges(deliveryChargesCents, getCurrentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartSummary(): Result<CartSummary?> {
        return try {
            val summary = cartDao.getCartSummary()?.toDomainModel()
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun calculateTotals(): Result<CartTotals> {
        return try {
            val items = cartDao.getCartItems().toDomainModels()
            val summary = cartDao.getCartSummary()?.toDomainModel()

            if (summary != null) {
                val totals = CartSummary.calculateTotals(items, summary)
                Result.success(totals)
            } else {
                Result.failure(Exception("Cart summary not initialized"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun initializeCart(phoneNumber: String): Result<Unit> {
        return try {
            cartDao.initializeCart(phoneNumber)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return try {
            cartDao.clearCart()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
