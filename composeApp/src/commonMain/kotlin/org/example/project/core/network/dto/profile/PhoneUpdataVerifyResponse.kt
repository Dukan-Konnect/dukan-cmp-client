package org.example.project.core.network.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class PhoneUpdateVerifyResponse(
    val message: String,
    val token: String
)