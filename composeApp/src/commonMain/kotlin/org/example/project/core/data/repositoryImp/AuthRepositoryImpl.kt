package org.example.project.core.data.repositoryImp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.example.project.core.data.repository.AuthRepository
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.network.dto.auth.LoginRequestDto
import org.example.project.core.network.dto.auth.VerifyRequestDto
import org.example.project.core.network.dto.auth.VerifyResponseDto
import org.example.project.core.network.services.AuthenticationService
import org.example.project.core.utils.DataState
import org.example.project.core.utils.safeApiCall

class AuthRepositoryImpl(
    private val authenticationService: AuthenticationService,
    private val userPreferencesRepository: UserPreferencesRepository,
) : AuthRepository {

    override suspend fun requestOtp(phoneNumber: String): DataState<String> = safeApiCall {
        val request = LoginRequestDto(phoneNumber)
        authenticationService.requestOtp(request)
    }

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String
    ): DataState<VerifyResponseDto> = safeApiCall {
        val request = VerifyRequestDto(phoneNumber, otp)
        val response = authenticationService.verifyOtp(request)

        userPreferencesRepository.saveToken(response.token)
        response
    }

}