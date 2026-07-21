package org.example.project.home.domain.repository

import org.example.project.core.model.home.Service
import org.example.project.core.utils.DataState

interface HomeRepository {
    val hasCache: Boolean
    fun getCachedPersonalServices(): List<Service>
    fun getCachedHomeServices(): List<Service>
    fun getCachedTrendingServices(): List<Service>

    suspend fun getPersonalServices(): DataState<List<Service>>
    suspend fun getHomeServices(): DataState<List<Service>>
    suspend fun getTrendingServices(): DataState<List<Service>>
}