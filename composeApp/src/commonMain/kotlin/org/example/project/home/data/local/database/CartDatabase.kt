package org.example.project.home.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.project.home.data.local.dao.AddressDao
import org.example.project.home.data.local.dao.BookingDao
import org.example.project.home.data.local.entities.BookingEntity
import org.example.project.home.data.local.entities.SavedAddressEntity

@Database(
    entities = [
        BookingEntity::class,
        SavedAddressEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class CartDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
    abstract fun addressDao(): AddressDao

    companion object {
        const val DATABASE_NAME = "cart_database.db"
    }
}
