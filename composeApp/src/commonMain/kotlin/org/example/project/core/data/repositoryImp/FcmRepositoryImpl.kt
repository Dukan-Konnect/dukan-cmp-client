package org.example.project.core.data.repositoryImp

import org.example.project.core.data.repository.FcmRepository
import org.example.project.core.network.dto.fcm.FcmTokenRequestDto
import org.example.project.core.network.services.FcmService
import org.example.project.core.utils.ApiCallHelper
import org.example.project.core.utils.DataState

class FcmRepositoryImpl(
    private val fcmService: FcmService,
    private val apiCallHelper: ApiCallHelper
) : FcmRepository {

    override suspend fun syncFcmToken(token: String): DataState<String> = apiCallHelper.execute {
        fcmService.syncFcmToken(FcmTokenRequestDto(token))
    }
}
