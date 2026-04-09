package org.example.project.core.network.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyResponseDto(
    val message: String,
    @SerialName("newUser")
    val isNewUser: Boolean,
    val token: String
)