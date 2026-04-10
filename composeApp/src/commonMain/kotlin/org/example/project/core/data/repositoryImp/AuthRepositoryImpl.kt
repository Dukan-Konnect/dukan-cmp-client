package org.example.project.core.data.repositoryImp

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.example.project.core.data.repository.AuthRepository
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.network.dto.auth.LoginRequestDto
import org.example.project.core.network.dto.auth.VerifyRequestDto
import org.example.project.core.network.dto.auth.VerifyResponseDto
import org.example.project.core.network.services.AuthenticationService
import org.example.project.core.utils.DataState

class AuthRepositoryImpl(
    private val authenticationService: AuthenticationService,
    private val userPreferencesRepository: UserPreferencesRepository,
) : AuthRepository {

    override suspend fun requestOtp(phoneNumber: String): DataState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequestDto(phoneNumber)
                val response = authenticationService.requestOtp(request)
                DataState.Success(response)
            } catch (e: ClientRequestException) {
                DataState.Error(Exception(extractErrorMessage(e.response)))
            } catch (e: IOException) {
                DataState.Error(Exception("Network error: ${e.message ?: "Please check your connection"}"))
            } catch (e: ServerResponseException) {
                DataState.Error(Exception("Server error: ${e.message ?: "Please try again later"}"))
            } catch (e: Exception) {
                DataState.Error(Exception(e.message ?: "Something went wrong", e))
            }
        }
    }

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String
    ): DataState<VerifyResponseDto> {
        return withContext(Dispatchers.IO) {
            try {
                val request = VerifyRequestDto(phoneNumber, otp)
                val response = authenticationService.verifyOtp(request)

                userPreferencesRepository.saveToken(response.token)

                DataState.Success(response)
            } catch (e: ClientRequestException) {
                DataState.Error(Exception(extractErrorMessage(e.response)))
            } catch (e: IOException) {
                DataState.Error(Exception("Network error: ${e.message ?: "Please check your connection"}"))
            } catch (e: ServerResponseException) {
                DataState.Error(Exception("Server error: ${e.message ?: "Please try again later"}"))
            } catch (e: Exception) {
                DataState.Error(Exception(e.message ?: "Something went wrong", e))
            }
        }
    }

    private suspend fun extractErrorMessage(response: HttpResponse): String {
        return try {
            response.bodyAsText()
        } catch (e: Exception) {
            "Invalid request"
        }
    }

    override suspend fun createAccount(phoneNumber: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            Result.success(true)
        }
}
