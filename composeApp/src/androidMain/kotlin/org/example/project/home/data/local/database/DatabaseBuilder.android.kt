package org.example.project.home.data.local.database

import android.content.Context
import androidx.room.Room

fun createCartDatabase(context: Context): CartDatabase {
    return Room.databaseBuilder(
        context = context,
        klass = CartDatabase::class.java,
        name = CartDatabase.DATABASE_NAME
    )
    .fallbackToDestructiveMigration()
    .build()
}

//// This will be called from Koin module with context
//actual fun getDatabaseBuilder(): CartDatabase {
//    throw IllegalStateException("Use createCartDatabase(context) instead")
//}
