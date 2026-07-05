package org.example.project.core.network.services

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import org.example.project.core.network.dto.fcm.FcmTokenRequestDto
import org.example.project.core.utils.ApiEndPoints

interface FcmService {

    @POST(ApiEndPoints.USERS + "/fcm-token")
    suspend fun syncFcmToken(
        @Body request: FcmTokenRequestDto
    ): String
}
