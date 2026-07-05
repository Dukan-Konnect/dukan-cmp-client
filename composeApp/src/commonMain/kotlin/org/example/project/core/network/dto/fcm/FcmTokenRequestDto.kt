package org.example.project.core.network.dto.fcm

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequestDto(
    val fcmToken: String
)
