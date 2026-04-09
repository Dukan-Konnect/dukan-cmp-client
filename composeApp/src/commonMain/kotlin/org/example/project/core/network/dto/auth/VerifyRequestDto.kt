package org.example.project.core.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class VerifyRequestDto(
    val phoneNumber: String,
    val otp: String
)