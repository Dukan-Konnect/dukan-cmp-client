package org.example.project.core.data.repository

import org.example.project.core.model.home.Service
import org.example.project.core.utils.DataState

interface HomeRepository {
    suspend fun getPersonalServices(): DataState<List<Service>>
    suspend fun getHomeServices(): DataState<List<Service>>
    suspend fun getTrendingServices(): DataState<List<Service>>
}