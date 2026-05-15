package org.example.project.core.data.repository

import org.example.project.core.network.dto.auth.VerifyResponseDto
import org.example.project.core.utils.DataState

interface AuthRepository {
    suspend fun requestOtp(phoneNumber: String): DataState<String>

    suspend fun verifyOtp(phoneNumber: String, otp: String): DataState<VerifyResponseDto>

}