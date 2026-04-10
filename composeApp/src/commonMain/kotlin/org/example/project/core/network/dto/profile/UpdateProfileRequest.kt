package org.example.project.core.network.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val name: String?,
    val email: String?,
    val phoneNumber : String?
)