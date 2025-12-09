package org.example.project.home.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.example.project.home.data.local.entities.CartItemEntity
import org.example.project.home.data.local.entities.CartSummaryEntity
import org.example.project.home.data.local.util.getCurrentTimeMillis

@Dao
interface CartDao {

    // Cart Items Operations
    @Query("SELECT * FROM cart_items ORDER BY created_at ASC")
    fun observeCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items ORDER BY created_at ASC")
    suspend fun getCartItems(): List<CartItemEntity>

    @Query("SELECT * FROM cart_items WHERE product_id = :productId")
    suspend fun getCartItem(productId: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateItem(item: CartItemEntity)

    @Upsert
    suspend fun upsertItem(item: CartItemEntity)

    @Upsert
    suspend fun upsertItems(items: List<CartItemEntity>)


    @Query("DELETE FROM cart_items WHERE product_id = :productId")
    suspend fun deleteItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearAllItems()

    @Query("SELECT COUNT(*) FROM cart_items")
    suspend fun getItemCount(): Int

    @Query("SELECT SUM(price_cents + provider_fee_cents) FROM cart_items")
    suspend fun getTotalAmount(): Long?

    // Cart Summary Operations
    @Query("SELECT * FROM cart_summary WHERE id = 1")
    fun observeCartSummary(): Flow<CartSummaryEntity?>

    @Query("SELECT * FROM cart_summary WHERE id = 1")
    suspend fun getCartSummary(): CartSummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSummary(summary: CartSummaryEntity)

    @Query("UPDATE cart_summary SET phone_number = :phoneNumber, updated_at = :updatedAt WHERE id = 1")
    suspend fun updatePhoneNumber(phoneNumber: String, updatedAt: Long)

    @Query("UPDATE cart_summary SET address = :address, updated_at = :updatedAt WHERE id = 1")
    suspend fun updateAddress(address: String?, updatedAt: Long)

    @Query("UPDATE cart_summary SET time_slot = :timeSlot, updated_at = :updatedAt WHERE id = 1")
    suspend fun updateTimeSlot(timeSlot: String?, updatedAt: Long)

    @Query("UPDATE cart_summary SET tax_percent = :taxPercent, updated_at = :updatedAt WHERE id = 1")
    suspend fun updateTaxPercent(taxPercent: Double, updatedAt: Long)

    @Query("UPDATE cart_summary SET delivery_charges_cents = :deliveryChargesCents, updated_at = :updatedAt WHERE id = 1")
    suspend fun updateDeliveryCharges(deliveryChargesCents: Long, updatedAt: Long)

    @Query("DELETE FROM cart_summary")
    suspend fun clearSummary()

    // Transaction for clearing entire cart
    @Transaction
    suspend fun clearCart() {
        clearAllItems()
        clearSummary()
    }

    // Transaction for initializing cart with phone number
    @Transaction
    suspend fun initializeCart(phoneNumber: String) {
        val existingSummary = getCartSummary()
        if (existingSummary == null) {
            val now = getCurrentTimeMillis()
            insertOrUpdateSummary(
                CartSummaryEntity(
                    phoneNumber = phoneNumber,
                    createdAt = now,
                    updatedAt = now
                )
            )
        } else {
            updatePhoneNumber(phoneNumber, getCurrentTimeMillis())
        }
    }
}
