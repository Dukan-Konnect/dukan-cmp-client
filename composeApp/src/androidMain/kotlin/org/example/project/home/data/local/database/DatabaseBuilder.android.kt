package org.example.project.home.data.local.database

import android.content.Context
import androidx.room.Room
import org.example.project.core.data.database.AppDatabase

fun createAppDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context = context,
        klass = AppDatabase::class.java,
        name = AppDatabase.DATABASE_NAME
    )
    .fallbackToDestructiveMigration()
    .build()
}
