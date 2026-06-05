package org.example.project.profile.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.profile.domain.model.SavedAddress

interface AddressRepository {
    fun observeAddresses(): Flow<List<SavedAddress>>
    suspend fun getAddressById(id: String): Result<SavedAddress?>
    suspend fun upsertAddress(address: SavedAddress): Result<Unit>
    suspend fun deleteAddress(id: String): Result<Unit>
}

