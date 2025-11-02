package org.example.project.onboarding.domain.repository

interface AuthRepository {
    suspend fun sendOtp(phoneNumber: String): Result<Boolean>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<String>
    suspend fun createAccount(phoneNumber: String): Result<Boolean>
}
