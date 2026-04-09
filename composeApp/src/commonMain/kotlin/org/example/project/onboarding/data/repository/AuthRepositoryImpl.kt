package org.example.project.onboarding.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.log
import org.example.project.core.network.dto.auth.LoginRequestDto
import org.example.project.core.network.dto.auth.VerifyRequestDto
import org.example.project.core.network.dto.auth.VerifyResponseDto
import org.example.project.core.network.services.AuthenticationService
import org.example.project.core.utils.DataState
import org.example.project.onboarding.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authenticationService: AuthenticationService,
    private val userPreferencesRepository: UserPreferencesRepository
) : AuthRepository {

    override suspend fun requestOtp(phoneNumber: String): DataState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequestDto(phoneNumber)
                val response = authenticationService.requestOtp(request)

                DataState.Success(response)
            } catch (e: Exception) {
                DataState.Error(exception = mapException(e))
            }
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): DataState<VerifyResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                val request = VerifyRequestDto(phoneNumber, otp)
                val response = authenticationService.verifyOtp(request)

                userPreferencesRepository.saveToken(response.token)
                userPreferencesRepository.setLoggedIn(true)

                DataState.Success(response)
            } catch (e: Exception) {
                DataState.Error(exception = mapException(e))
            }
        }
    }

    private fun mapException(e: Exception): Exception {
        val userFriendlyMessage = when (e) {
            is ClientRequestException -> "Invalid OTP or request. Please try again."
            is ServerResponseException -> "Server is currently unavailable."
            is IOException -> "Please check your internet connection."
            else -> e.message ?: "An unexpected error occurred."
        }
        return Exception(userFriendlyMessage, e)
    }


    override suspend fun createAccount(phoneNumber: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext Result.success(true)
    }
//        try {
//            supabase.auth.signInWith(Email) {
//                email = "kartikeyshukla15@gmail.com"
//                password = "example-password"
//            }
//            return@withContext Result.success(true)
//
//        } catch (t: Throwable) {
//            return@withContext Result.failure(t)
//        }
//    }
}
