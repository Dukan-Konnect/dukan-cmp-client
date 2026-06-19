package org.example.project.core.data.repositoryImp

import org.example.project.core.data.repository.AuthRepository
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.network.dto.auth.LoginRequestDto
import org.example.project.core.network.dto.auth.VerifyRequestDto
import org.example.project.core.network.dto.auth.VerifyResponseDto
import org.example.project.core.network.services.AuthenticationService
import org.example.project.core.utils.ApiCallHelper
import org.example.project.core.utils.DataState

class AuthRepositoryImpl(
    private val authenticationService: AuthenticationService,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val apiCallHelper: ApiCallHelper
) : AuthRepository {

    override suspend fun requestOtp(phoneNumber: String): DataState<String> = apiCallHelper.execute {
        val request = LoginRequestDto(phoneNumber)
        authenticationService.requestOtp(request)
    }

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String
    ): DataState<VerifyResponseDto> = apiCallHelper.execute {
        val request = VerifyRequestDto(phoneNumber, otp)
        val response = authenticationService.verifyOtp(request)

        userPreferencesRepository.updateToken(response.token)
        response
    }

}