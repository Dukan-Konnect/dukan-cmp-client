package org.example.project.profile.data.repository

import kotlin.math.abs
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.profile.domain.model.SavedAddress
import org.example.project.profile.domain.repository.AddressRepository
import kotlin.time.Clock

class InMemoryAddressRepository : AddressRepository {
    private val addressesFlow = MutableStateFlow<List<SavedAddress>>(emptyList())

    override fun observeAddresses(): Flow<List<SavedAddress>> = addressesFlow.asStateFlow()

    override suspend fun getAddressById(id: String): Result<SavedAddress?> = runCatching {
        addressesFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun upsertAddress(address: SavedAddress): Result<Unit> = runCatching {
        val addressId = address.id.takeIf { it.isNotBlank() } ?: generateAddressId()
        addressesFlow.update { current ->
            val updated = current.filterNot { it.id == addressId }
            val normalized = address.copy(id = addressId)
            if (normalized.isDefault) {
                updated.map { it.copy(isDefault = false) } + normalized
            } else {
                updated + normalized
            }
        }
    }

    override suspend fun deleteAddress(id: String): Result<Unit> = runCatching {
        addressesFlow.update { current ->
            val remaining = current.filterNot { it.id == id }
            if (remaining.none { it.isDefault } && remaining.isNotEmpty()) {
                remaining.mapIndexed { index, item ->
                    if (index == 0) item.copy(isDefault = true) else item
                }
            } else {
                remaining
            }
        }
    }

    private fun generateAddressId(): String {
        return "addr_ios_${Clock.System.now().toEpochMilliseconds()}_${abs(Random.nextLong())}"
    }
}
