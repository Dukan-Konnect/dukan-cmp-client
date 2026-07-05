package org.example.project.core.data.repository

import org.example.project.core.utils.DataState

interface FcmRepository {

    suspend fun syncFcmToken(token: String): DataState<String>
}
