package org.example.project.core.data.repositoryImp

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.example.project.core.data.mapper.toUiModel
import org.example.project.core.data.repository.ProfileRepository
import org.example.project.core.model.profile.Profile
import org.example.project.core.network.dto.profile.UpdateProfileRequest
import org.example.project.core.network.services.ProfileService
import org.example.project.core.utils.DataState

class ProfileRepositoryImpl(
    private val profileService: ProfileService,
) : ProfileRepository {

    override suspend fun getProfile(): DataState<Profile> {
        return withContext(Dispatchers.IO) {
            try {
                val dto = profileService.getMyProfile()
                DataState.Success(dto.toUiModel())
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

    override suspend fun updateNameAndEmail(
        name: String?,
        email: String?,
        phoneNumber : String?
    ): DataState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = profileService.updateProfile(UpdateProfileRequest(name, email,phoneNumber))
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

    override suspend fun finalizePhoneUpdate(
        newPhone: String,
        otp: String
    ): DataState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = mapOf("phoneNumber" to newPhone, "otp" to otp)
                val response = profileService.verifyPhoneUpdate(requestBody)

                DataState.Success(response.message)
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
}