package org.example.project.profile.data.repository

import kotlin.math.abs
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.home.data.local.dao.AddressDao
import org.example.project.home.data.local.mappers.toDomain
import org.example.project.home.data.local.mappers.toEntity
import org.example.project.home.data.local.util.getCurrentTimeMillis
import org.example.project.profile.domain.model.SavedAddress
import org.example.project.profile.domain.repository.AddressRepository

class AddressRepositoryImpl(
    private val addressDao: AddressDao
) : AddressRepository {

    override fun observeAddresses(): Flow<List<SavedAddress>> {
        return addressDao.observeAddresses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAddressById(id: String): Result<SavedAddress?> = runCatching {
        addressDao.getAddressById(id)?.toDomain()
    }

    override suspend fun upsertAddress(address: SavedAddress): Result<Unit> = runCatching {
        val existing = if (address.id.isBlank()) null else addressDao.getAddressById(address.id)
        val now = getCurrentTimeMillis()
        val addressId = existing?.id ?: address.id.takeIf { it.isNotBlank() } ?: generateAddressId()
        val shouldBeDefault = address.isDefault

        if (shouldBeDefault) {
            addressDao.clearDefaultFlags()
        }

        addressDao.upsertAddress(
            address.copy(
                id = addressId,
                isDefault = shouldBeDefault
            ).toEntity(
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )
        )
    }

    override suspend fun deleteAddress(id: String): Result<Unit> = runCatching {
        val existing = addressDao.getAddressById(id)
        addressDao.deleteAddress(id)

        if (existing?.isDefault == true) {
            val firstRemaining = addressDao.getAddresses().firstOrNull() ?: return@runCatching
            val now = getCurrentTimeMillis()
            addressDao.clearDefaultFlags()
            addressDao.upsertAddress(
                firstRemaining.copy(
                    isDefault = true,
                    updatedAt = now
                )
            )
        }
    }

    private fun generateAddressId(): String {
        return "addr_${getCurrentTimeMillis()}_${abs(Random.nextLong())}"
    }
}
