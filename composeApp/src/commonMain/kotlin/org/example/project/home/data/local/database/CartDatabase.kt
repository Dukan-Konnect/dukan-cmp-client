package org.example.project.home.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.project.home.data.local.dao.CartDao
import org.example.project.home.data.local.entities.CartItemEntity
import org.example.project.home.data.local.entities.CartSummaryEntity

@Database(
    entities = [
        CartItemEntity::class,
        CartSummaryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        const val DATABASE_NAME = "cart_database.db"
    }
}
