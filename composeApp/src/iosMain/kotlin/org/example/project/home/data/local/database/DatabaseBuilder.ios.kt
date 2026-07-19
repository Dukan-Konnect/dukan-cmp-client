package org.example.project.home.data.local.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.cinterop.ExperimentalForeignApi
import org.example.project.core.data.database.AppDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSSearchPathForDirectoriesInDomains

@OptIn(ExperimentalForeignApi::class)
fun createAppDatabase(): AppDatabase {
    val dbFileName = databasePath(AppDatabase.DATABASE_NAME)

    return Room.databaseBuilder<AppDatabase>(name = dbFileName)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
}

@OptIn(ExperimentalForeignApi::class)
private fun databasePath(fileName: String): String {
    val documentDirectory = NSSearchPathForDirectoriesInDomains(
        NSDocumentDirectory,
        NSUserDomainMask,
        true
    ).firstOrNull() as? String

    require(!documentDirectory.isNullOrBlank()) {
        "Unable to resolve the iOS documents directory for Room database storage."
    }

    return NSFileManager.defaultManager
        .URLForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask,
            null,
            true,
            null
        )
        ?.path
        ?.let { "$it/$fileName" }
        ?: "$documentDirectory/$fileName"
}
