package org.example.project.core.network.services

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import org.example.project.core.network.dto.profile.PhoneUpdateVerifyResponse
import org.example.project.core.network.dto.profile.UpdateProfileRequest
import org.example.project.core.network.dto.profile.UserProfileDto
import org.example.project.core.utils.ApiEndPoints

interface ProfileService {

    @GET(ApiEndPoints.PROFILE)
    suspend fun getMyProfile(): UserProfileDto

    @PUT(ApiEndPoints.PROFILE)
    suspend fun updateProfile(@Body request: UpdateProfileRequest): String

    @POST(ApiEndPoints.PROFILE + "/phone/request")
    suspend fun requestPhoneUpdate(@Body phoneNumber: String): String

    @POST(ApiEndPoints.PROFILE + "/phone/verify")
    suspend fun verifyPhoneUpdate(@Body request: Map<String, String>): PhoneUpdateVerifyResponse
}