package org.example.project.core.data.mapper

import org.example.project.core.model.profile.Profile
import org.example.project.core.network.dto.profile.UserProfileDto

fun UserProfileDto.toUiModel(): Profile {
    return Profile(
        fullName = this.name ?: "Unknown User",
        displayEmail = this.email ?: "No email provided",
        formattedPhone = "+91 ${this.phoneNumber.chunked(5).joinToString(" ")}",
        canUpdate = !this.name.isNullOrBlank()
    )
}