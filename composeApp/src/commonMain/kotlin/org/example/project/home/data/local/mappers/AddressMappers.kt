package org.example.project.home.data.local.mappers

import org.example.project.home.data.local.entities.SavedAddressEntity
import org.example.project.home.data.local.util.getCurrentTimeMillis
import org.example.project.profile.domain.model.SavedAddress

fun SavedAddressEntity.toDomain(): SavedAddress = SavedAddress(
    id = id,
    label = label,
    houseNumber = houseNumber,
    street = street,
    city = city,
    state = state,
    landmark = landmark,
    phone = phone,
    isDefault = isDefault
)

fun SavedAddress.toEntity(
    createdAt: Long = getCurrentTimeMillis(),
    updatedAt: Long = getCurrentTimeMillis()
): SavedAddressEntity = SavedAddressEntity(
    id = id,
    label = label,
    houseNumber = houseNumber,
    street = street,
    city = city,
    state = state,
    landmark = landmark,
    phone = phone,
    isDefault = isDefault,
    createdAt = createdAt,
    updatedAt = updatedAt
)
