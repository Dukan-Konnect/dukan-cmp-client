package org.example.project.profile.domain.repository

import org.example.project.core.model.profile.Profile
import org.example.project.core.utils.DataState

interface ProfileRepository {
    suspend fun getProfile(): DataState<Profile>

    suspend fun updateNameAndEmail(
        name: String?,
        email: String?,
        phoneNumber: String?
    ): DataState<String>

    suspend fun finalizePhoneUpdate(
        newPhone: String,
        otp: String
    ): DataState<String>
}