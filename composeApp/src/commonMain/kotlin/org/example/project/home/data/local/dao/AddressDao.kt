package org.example.project.home.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.example.project.home.data.local.entities.SavedAddressEntity

@Dao
interface AddressDao {
    @Query("SELECT * FROM saved_addresses ORDER BY is_default DESC, updated_at DESC")
    fun observeAddresses(): Flow<List<SavedAddressEntity>>

    @Query("SELECT * FROM saved_addresses ORDER BY is_default DESC, updated_at DESC")
    suspend fun getAddresses(): List<SavedAddressEntity>

    @Query("SELECT * FROM saved_addresses WHERE id = :id")
    suspend fun getAddressById(id: String): SavedAddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAddress(address: SavedAddressEntity)

    @Query("DELETE FROM saved_addresses WHERE id = :id")
    suspend fun deleteAddress(id: String)

    @Query("UPDATE saved_addresses SET is_default = 0")
    suspend fun clearDefaultFlags()
}

