package org.example.project.core.network.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val phoneNumber: String,
    val name: String?,
    val email: String?
)