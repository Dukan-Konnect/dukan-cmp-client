package org.example.project.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.project.profile.data.local.dao.AddressDao
import org.example.project.booking.data.local.dao.BookingDao
import org.example.project.booking.data.local.entities.BookingEntity
import org.example.project.profile.data.local.entities.SavedAddressEntity

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
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
    abstract fun addressDao(): AddressDao

    companion object {
        const val DATABASE_NAME = "app_database.db"
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
