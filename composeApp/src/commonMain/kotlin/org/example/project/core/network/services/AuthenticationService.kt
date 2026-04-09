package org.example.project.core.network.services

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import org.example.project.core.network.dto.auth.LoginRequestDto
import org.example.project.core.network.dto.auth.VerifyRequestDto
import org.example.project.core.network.dto.auth.VerifyResponseDto
import org.example.project.core.utils.ApiEndPoints

interface AuthenticationService {

    @POST(ApiEndPoints.AUTHENTICATION + "/login")
    suspend fun requestOtp(
        @Body request: LoginRequestDto
    ): String

    @POST(ApiEndPoints.AUTHENTICATION + "/verify")
    suspend fun verifyOtp(
        @Body request: VerifyRequestDto
    ): VerifyResponseDto
}






