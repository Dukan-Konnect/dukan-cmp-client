package org.example.project.profile.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class SavedAddress(
    val id: String,
    val label: String,
    val houseNumber: String,
    val street: String,
    val city: String,
    val state: String,
    val landmark: String,
    val phone: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false
)
