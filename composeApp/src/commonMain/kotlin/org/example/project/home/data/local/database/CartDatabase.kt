package org.example.project.home.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.project.home.data.local.dao.AddressDao
import org.example.project.home.data.local.dao.BookingDao
import org.example.project.home.data.local.dao.CartDao
import org.example.project.home.data.local.entities.BookingEntity
import org.example.project.home.data.local.entities.CartItemEntity
import org.example.project.home.data.local.entities.CartSummaryEntity
import org.example.project.home.data.local.entities.SavedAddressEntity

@Database(
    entities = [
        CartItemEntity::class,
        CartSummaryEntity::class,
        BookingEntity::class,
        SavedAddressEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun bookingDao(): BookingDao
    abstract fun addressDao(): AddressDao

    companion object {
        const val DATABASE_NAME = "cart_database.db"
    }
}
