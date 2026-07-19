package org.example.project.home.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.project.home.data.local.dao.AddressDao
import org.example.project.home.data.local.dao.BookingDao
import org.example.project.home.data.local.entities.BookingEntity
import org.example.project.home.data.local.entities.SavedAddressEntity

import androidx.room.ConstructedBy
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [
        BookingEntity::class,
        SavedAddressEntity::class
    ],
    version = 7,
    exportSchema = false
)
@ConstructedBy(CartDatabaseConstructor::class)
abstract class CartDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
    abstract fun addressDao(): AddressDao

    companion object {
        const val DATABASE_NAME = "cart_database.db"
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object CartDatabaseConstructor : RoomDatabaseConstructor<CartDatabase>
